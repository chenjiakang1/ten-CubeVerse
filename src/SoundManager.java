import javax.sound.sampled.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SoundManager {

    // 资源路径（可按需修改）
    public static String MOVE_SOUND = "/sounds/move.wav";
    public static String DESTROY_SOUND = "/sounds/destroy.wav";

    /** 缓存：存已解码的 PCM 数据与格式（不是 Clip，本类每次播放都会新建 Clip） */
    private static final Map<String, CachedAudio> AUDIO_CACHE = new ConcurrentHashMap<>();

    /** 全局音量（分贝，0 为原始，负值更小，如 -10f），对“之后的播放”生效 */
    private static volatile float GLOBAL_GAIN_DB = -10f;

    /** 简单的封装结构 */
    private static class CachedAudio {
        final AudioFormat format;
        final byte[] data;
        CachedAudio(AudioFormat format, byte[] data) {
            this.format = format;
            this.data = data;
        }
    }

    /** 播放移动音效 */
    public static void playMove() { play(MOVE_SOUND); }

    /** 播放销毁音效 */
    public static void playDestroy() { play(DESTROY_SOUND); }

    /** 设置全局音量（dB，负值更小，0 原音量，例如 -10f） */
    public static void setGlobalGain(float gainDb) { GLOBAL_GAIN_DB = gainDb; }

    /** 播放指定资源（每次调用都新建 Clip；失败不会阻塞 UI） */
    public static void play(String resourcePath) {
        new Thread(() -> {
            try {
                CachedAudio ca = loadPcm(resourcePath);
                if (ca == null) {
                    System.err.println("⚠️ 找不到音频资源: " + resourcePath);
                    return;
                }

                Clip clip = AudioSystem.getClip();
                clip.open(ca.format, ca.data, 0, ca.data.length);

                // 应用全局音量（若声卡支持）
                applyGain(clip, GLOBAL_GAIN_DB);

                clip.start();
                clip.addLineListener(ev -> {
                    if (ev.getType() == LineEvent.Type.STOP) {
                        try { clip.close(); } catch (Exception ignore) {}
                    }
                });
            } catch (LineUnavailableException e) {
                System.err.println("音频设备不可用: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("音频播放失败: " + resourcePath + " -> " + e.getMessage());
            }
        }, "SoundPlay-" + resourcePath).start();
    }

    /** 读取并缓存为 PCM_SIGNED 16-bit 的音频数据（若已缓存则直接返回） */
    private static CachedAudio loadPcm(String resourcePath) {
        CachedAudio cached = AUDIO_CACHE.get(resourcePath);
        if (cached != null) return cached;

        try {
            URL url = SoundManager.class.getResource(resourcePath);
            if (url == null) return null;

            // 原始流（可能是 WAV/AIFF/等，也可能已是 PCM）
            AudioInputStream in = AudioSystem.getAudioInputStream(url);
            AudioFormat base = in.getFormat();

            // 目标格式：PCM_SIGNED 16-bit，保证可被 Clip 可靠播放
            AudioFormat target = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    base.getSampleRate(),
                    16,
                    base.getChannels(),
                    base.getChannels() * 2,
                    base.getSampleRate(),
                    false // little-endian
            );

            AudioInputStream pcmStream =
                    AudioSystem.isConversionSupported(target, base)
                            ? AudioSystem.getAudioInputStream(target, in)
                            : in;

            byte[] data = readAllBytes(pcmStream);
            closeQuietly(pcmStream);
            closeQuietly(in);

            CachedAudio ca = new CachedAudio(pcmStream.getFormat(), data);
            AUDIO_CACHE.put(resourcePath, ca);
            return ca;
        } catch (UnsupportedAudioFileException | IOException e) {
            System.err.println("加载音频失败: " + resourcePath + " -> " + e.getMessage());
            return null;
        }
    }

    /** 读取流全部字节 */
    private static byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(4096);
        byte[] buf = new byte[4096];
        int n;
        while ((n = is.read(buf)) != -1) bos.write(buf, 0, n);
        return bos.toByteArray();
    }

    /** 应用增益（若支持） */
    private static void applyGain(Clip clip, float gainDb) {
        try {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                // 限制在可用范围内
                float clamped = Math.max(gain.getMinimum(), Math.min(gain.getMaximum(), gainDb));
                gain.setValue(clamped);
            }
        } catch (Exception ignore) {}
    }

    private static void closeQuietly(AutoCloseable c) {
        if (c == null) return;
        try { c.close(); } catch (Exception ignore) {}
    }
}
