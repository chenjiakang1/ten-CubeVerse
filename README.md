# ten-CubeVerse
CubeVerse is an experimental block elimination game developed in Java. The project focuses on exploring UI animation, button swapping mechanics, and interactive game design for future expansion into a complete puzzle experience.

---

## 🎮 Features

### 🧩 Core Gameplay
- **Grid-Based Puzzle System** – Dynamically generates a centered grid layout based on difficulty level.  
- **Match-3 Elimination** – Adjacent blocks of the same type automatically disappear when three or more connect.  
- **Gravity System** – After elimination, blocks smoothly **fall down with 0.5s animation** to fill empty spaces.  
- **Block Swapping** – Click one block to highlight it, then click an adjacent block to swap their positions.  
  - Non-adjacent clicks automatically **cancel selection** and remove the highlight.

---

### 🎨 UI & Animation
- **Smooth Swap Animation** – Each swap action includes a polished transition with controlled timing.  
- **Falling Animation** – Blocks drop using a timer-based smooth interpolation.  
- **Centered Layout** – All grids automatically adjust position both **horizontally and vertically** according to difficulty.  
- **Dynamic Highlight Effect** – The selected block glows with a yellow border to indicate readiness.

---

### 🔊 Sound Effects
- Integrated **`SoundManager`** for immersive feedback:  
  - Move sound on block swapping  
  - Destroy sound on elimination  

---

### ⚙️ Difficulty & Menu System
- **Difficulty Selection** – Different grid sizes and total block counts for multiple difficulty levels:  
  - Easy (5×5), Normal (6×6), Hard (7×7), Expert (8×8)  
- **Main Menu** – Launch window with difficulty selection and start options.  
- **Return Button** – Every game screen includes a “Back to Menu” button and **ESC key shortcut**.

---

### 💡 Technical Highlights
- **Object-Oriented Design** with modular class separation:
  - `ImageButton.java` – Custom grid-aware button class  
  - `SwapManager.java` – Handles click logic and swap validation  
  - `Match3Manager.java` – Detects and removes matching blocks  
  - `MainWindow.java` – Manages UI generation and difficulty layout  
  - `SoundManager.java` – Controls sound effects  
- **Smooth Timer-based Animations** using `javax.swing.Timer`
- **Responsive Layout** independent of screen resolution

---
