// MainMenu.java
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainMenu extends JPanel {
    private JFrame parentFrame;
    private CardLayout cardLayout;
    private JPanel mainContainer;
    
    public MainMenu(JFrame frame, CardLayout layout, JPanel container) {
        this.parentFrame = frame;
        this.cardLayout = layout;
        this.mainContainer = container;
        
        setLayout(new BorderLayout());
        setOpaque(false); // Transparent background
        
        // Create menu interface
        createMenu();
    }
    
    private void createMenu() {
        // Main panel - using vertical layout
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(150, 0, 0, 0));
        
        // Title
        JLabel titleLabel = new JLabel("CubeVerse", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 15));
        buttonPanel.setOpaque(false);
        buttonPanel.setMaximumSize(new Dimension(200, 150));
        
        // Create buttons
        JButton startButton = createMenuButton("Start Game");
        JButton settingsButton = createMenuButton("Settings");
        JButton exitButton = createMenuButton("Exit Game");
        
        // Add button listeners
        startButton.addActionListener(new StartGameListener());
        settingsButton.addActionListener(new SettingsListener());
        exitButton.addActionListener(new ExitListener());
        
        // Assemble button panel
        buttonPanel.add(startButton);
        buttonPanel.add(settingsButton);
        buttonPanel.add(exitButton);
        
        // Assemble menu panel
        menuPanel.add(titleLabel);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 50))); // Spacing
        menuPanel.add(buttonPanel);
        
        // Add to main panel
        add(menuPanel, BorderLayout.CENTER);
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 18));
        button.setBackground(new Color(70, 130, 180)); // Steel blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        button.setPreferredSize(new Dimension(200, 50));
        
        // Mouse hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(100, 149, 237)); // Cornflower blue
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(70, 130, 180)); // Restore original color
            }
        });
        
        return button;
    }
    
    // Start game button listener
    private class StartGameListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Switch to game interface
            cardLayout.show(mainContainer, "GAME");
        }
    }
    
    // Settings button listener
    private class SettingsListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(parentFrame, 
                "Settings feature under development...", 
                "Settings", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Exit button listener
    private class ExitListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int result = JOptionPane.showConfirmDialog(parentFrame,
                "Are you sure you want to exit the game?",
                "Exit Game",
                JOptionPane.YES_NO_OPTION);
                
            if (result == JOptionPane.YES_OPTION) {
                System.exit(0);
            }
        }
    }
}