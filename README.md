# ArcadeTUI

A retro-style terminal arcade system with persistent scoreboards and modular game cartridges.

## 🕹️ Features

- **Retro TUI Interface**: Beautiful ASCII art with vintage styling
- **Modular Cartridge System**: Easy to add new games as virtual cartridges
- **Persistent Scoreboards**: JSON-based high score tracking for each game
- **Two Classic Games**:
  - 🧱 **TETRIS**: Classic falling blocks puzzle with full rotation and line clearing
  - 🚀 **GALAGA**: Space shooter with enemies, bullets, and progressive levels

## 🎮 Controls

### Main Menu
- `↑↓` - Navigate between game cartridges
- `ENTER` - Launch selected game
- `S` - View high scores
- `Q` - Quit arcade

### Tetris Controls
- `A/D` - Move pieces left/right
- `S` - Soft drop (move down faster)
- `W` - Rotate piece
- `Q` - Quit to main menu

### Galaga Controls
- `A/D` - Move ship left/right
- `SPACE` - Shoot
- `Q` - Quit to main menu

## 🏗️ Building and Running

### Prerequisites
- Java 21
- Kotlin 2.2.20
- Gradle

### Build and Run
```bash
# Clone the repository
git clone https://github.com/OmyDaGreat/ArcadeTUI.git
cd ArcadeTUI

# Build the project
gradle build

# Run the arcade
gradle run
```

## 🎯 Game Features

### Tetris
- All 7 standard Tetris pieces (I, O, T, S, Z, J, L)
- Piece rotation with collision detection
- Line clearing with scoring
- Progressive speed increase with levels
- Full game board rendering

### Galaga
- Player spaceship movement
- Enemy waves with different types
- Shooting mechanics with cooldown
- Collision detection
- Lives system
- Progressive difficulty levels

## 📊 Scoreboard System

High scores are automatically saved to `arcade-data/` directory:
- `tetris-scores.json` - Tetris high scores
- `galaga-scores.json` - Galaga high scores

Each score entry includes:
- Player name (up to 10 characters)
- Final score
- Level reached
- Date and time

## 🔧 Adding New Games

To add a new game cartridge:

1. Create a class implementing the `GameCartridge` interface:

```kotlin
class MyGameCartridge : GameCartridge {
    override val name = "MY GAME"
    override val description = "Description here"
    override val version = "1.0"
    override val icon = "🎮"
    
    override suspend fun play(arcade: ArcadeSystem) {
        // Implement your game logic here
    }
}
```

2. Add your cartridge to the main arcade system in `Runner.kt`:

```kotlin
arcade.addCartridge(MyGameCartridge())
```

## 🏛️ Architecture

The system follows a modular architecture:

- **ArcadeSystem**: Core system managing terminal I/O, menus, and cartridges
- **GameCartridge**: Interface for all games to implement
- **ScoreManager**: Handles persistent high score storage
- **Individual Games**: Tetris and Galaga implementations

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🚀 Future Enhancements

- More classic arcade games (Pac-Man, Space Invaders, etc.)
- Sound effects using terminal beep
- Game state saving/loading
- Tournament mode with timed competitions
- Network multiplayer support
- Configuration system for controls and settings