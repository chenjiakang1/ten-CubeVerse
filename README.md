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

## Game Rules
- The game board is a grid composed of colored blocks.
- The player may swap two adjacent blocks (up, down, left, right).
- A swap is valid only if it creates a match of at least three identical blocks.  
- Matched blocks are removed automatically.
- After removal:
  - Blocks above fall down due to gravity.
  - New blocks are generated at the top to fill empty spaces.
- Each elimination increases the player’s score or coins.
- Players may use items to influence the game.
- When the end condition is met, the game shows the result screen.

---

## Game Flow
- Launch the game
- Enter the main menu
- View game rules (optional)
- Start the game
- Play until the game ends
- Display end screen

---

## Core Features
- Grid-based block generation
- Adjacent block swapping
- Match-3 detection logic
- Automatic block elimination
- Gravity-based falling system
- Random block generation
- Score and coin system
- Item system
- Animation effects
- Sound effects
- Start menu, rule window, and end window

---

## Project Structure
### Entry & Windows
- **GameLauncher** : Entry point of the game.
- **MainMenu** : Displays the starting menu.
- **MainWindow / MainWindowBack** : Core game window and background management.
- **GameRulesWindow / GameRules2Window** : Displays game instructions.
- **EndWindow** : Displays the final result after the game ends.
### Game Logic Modules
- **SwapManager** : Handles adjacent block swapping and validation.
- **Match3Manager** : Detects match-3 or more blocks and triggers elimination.
- **ScoreManager / CoinManager** : Calculates and updates player scores or coins.
- **ItemManager** : Manages item usage and effects.
- **ItemType** : Enum defining different item types.
### Block & Animation
- **ImageButton** : Custom button representing each block on the grid.
- **BlockFactory** : Generates blocks using random or difficulty-based logic.
- **Animator** : Controls block movement, swapping, and falling animations.
### Sound
- **SoundManager** : Plays sound effects during interactions such as swapping or elimination.

---

## How to Run
- Clone the repository.
- Open the project using IntelliJ IDEA.
- Locate and run the GameLauncher class.
- The game window will open automatically.

---

## Screenshots

### Main Menu
![Menu](images/menu.png)

### Gameplay
![gameWindow](images/gameWindow.png)

---
## Future Improvements
- Daily challenge missions  
  Introduce daily missions with specific objectives to encourage regular player engagement.
- More item types  
  Add more item varieties with unique effects to enhance gameplay diversity and strategy.
- Special level design  
  Design special levels with rules such as time limits, target block clears, or restricted moves.
- Player leaderboard  
  Implement a leaderboard system to record and compare player scores.

