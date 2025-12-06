# ten-CubeVerse
This project is a grid-based Match-3 block elimination game developed in Java using Swing.
Players can swap adjacent blocks to form matches of three or more blocks of the same type.
The game includes scoring, items, animations, sound effects, and a complete game flow from start menu to end screen.

The project follows an object-oriented design and separates core functionalities into independent manager classes.

---

## Technology Stack

### Programming Language
- java8
### GUI Framework
- Java Swing
### Development Tools
- IntelliJ IDEA
- Git & GitHub

---

### Game Rules
- **Smooth Swap Animation** – Each swap action includes a polished transition with controlled timing.  
- **Falling Animation** – Blocks drop using a timer-based smooth interpolation.  
- **Centered Layout** – All grids automatically adjust position both **horizontally and vertically** according to difficulty.  
- **Dynamic Highlight Effect** – The selected block glows with a yellow border to indicate readiness.

---

### Sound Effects
- Integrated **`SoundManager`** for immersive feedback:  
  - Move sound on block swapping  
  - Destroy sound on elimination  

---

### Difficulty & Menu System
- **Difficulty Selection** – Different grid sizes and total block counts for multiple difficulty levels:  
  - Easy (5×5), Normal (6×6), Hard (7×7), Expert (8×8)  
- **Main Menu** – Launch window with difficulty selection and start options.  
- **Return Button** – Every game screen includes a “Back to Menu” button and **ESC key shortcut**.

---

### Technical Highlights
- **Object-Oriented Design** with modular class separation:
  - `ImageButton.java` – Custom grid-aware button class  
  - `SwapManager.java` – Handles click logic and swap validation  
  - `Match3Manager.java` – Detects and removes matching blocks  
  - `MainWindow.java` – Manages UI generation and difficulty layout  
  - `SoundManager.java` – Controls sound effects  
- **Smooth Timer-based Animations** using `javax.swing.Timer`
- **Responsive Layout** independent of screen resolution

---
