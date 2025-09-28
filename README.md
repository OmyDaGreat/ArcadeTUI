# ArcadeTUI

A retro-style terminal arcade system with persistent scoreboards, modular game cartridges, and **advanced theming system**.

## 🕹️ Features

- **Interactive TUI Interface**: Beautiful ASCII art with vintage styling powered by Lanterna
- **Full Interactive Navigation**: Menu-driven interface with keyboard navigation and real-time updates
- **Advanced Theming System**: 5 built-in themes + custom theme support with URL downloading
- **Interactive Theme Selector**: Live theme switching with instant preview
- **Modular Cartridge System**: Easy to add new games as virtual cartridges
- **Persistent Scoreboards**: JSON-based high score tracking for each game with interactive viewing
- **Two Classic Games**:
  - 🧱 **TETRIS**: Classic falling blocks puzzle with full rotation and line clearing
  - 🚀 **GALAGA**: Space shooter with enemies, bullets, and progressive levels

## 🎨 Theming System

### Built-in Themes
- **Classic**: Original ArcadeTUI cyan/yellow theme
- **Neon**: Bright cyberpunk colors (hot pink/bright green)
- **Minimal**: Clean monochrome design
- **Dark**: Muted colors for low-light environments  
- **Retro**: Classic 80s arcade orange/magenta palette

### Custom Theme Support
Create custom themes in YAML format:
```yaml
name: "Matrix"
description: "Green-on-black matrix theme"
colors:
  primary: "#00FF00"
  secondary: "#008000" 
  accent: "#00FF00"
  text: "#00FF00"
  # ... more colors
```

### URL Theme Downloading
Download themes from URLs (YAML or JSON):
```kotlin
// In code
themeManager.addThemeFromUrl("https://example.com/themes/matrix.yaml")

// Theme files are saved to arcade-data/themes/custom/
```

## 🎮 Controls

### Main Menu
- `↑↓` or `W/S` - Navigate between menu options
- `ENTER` - Select menu option
- `Q` - Quit arcade

### Interactive Features
- **Game Selection**: Browse and launch games directly from the main menu
- **High Scores**: View top scores for all games
- **Theme Selector**: Interactive theme switching with live preview
- **Error Handling**: User-friendly error messages with continue prompts

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

## 🛠️ Technology Stack

- **Terminal UI**: [Lanterna](https://github.com/mabe02/lanterna) - Mature Java terminal GUI library
- **Theme System**: Custom YAML-based theming with HTTP downloading
- **Serialization**: Kotlinx Serialization (JSON) + Kaml (YAML)
- **HTTP Client**: OkHttp for theme downloads
- **Coroutines**: Kotlinx Coroutines for async operations
- **Persistence**: JSON file storage for scores and themes

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

The system follows a modular, themed architecture:

### Core Components
- **ArcadeSystem**: Core system managing terminal I/O, menus, and cartridges (now Lanterna-based)
- **ThemeManager**: Handles theme loading, switching, URL downloads, and persistence
- **GameCartridge**: Interface for all games to implement
- **ScoreManager**: Handles persistent high score storage

### Theme System Architecture
- **Theme**: Data class representing a complete theme configuration
- **ThemeColors**: Color palette for all UI elements
- **LanternaThemeColors**: Lanterna-compatible color objects
- **Default Themes**: 5 built-in themes with different aesthetics
- **Custom Themes**: User-defined themes loaded from YAML/JSON files
- **URL Support**: HTTP downloading of themes from remote URLs
- **Persistence**: Themes stored in `arcade-data/themes/`

### Game Integration
Individual games can access themed colors through:
```kotlin
val theme = arcade.getThemeManager().currentTheme.toLanternaColors()
color(theme.player) { /* render player */ }
color(theme.enemy) { /* render enemies */ }
```

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🚀 Future Enhancements

### Completed ✅
- ✅ **Advanced Theming System** with 5 built-in themes
- ✅ **URL Theme Downloads** with YAML/JSON support  
- ✅ **Lanterna Integration** for modern TUI rendering
- ✅ **Theme Persistence** and management
- ✅ **Interactive Theme Selection Menu** with live preview
- ✅ **Interactive Main Menu** with keyboard navigation
- ✅ **Game Launching System** with error handling
- ✅ **Interactive High Scores Viewer** for all games

### Planned 🔄
- 🔄 **Game Theme Integration** - convert games to use themed colors consistently
- 🔄 **Theme Editor UI** for creating custom themes interactively
- 🔄 **Enhanced Menu Animations** with smooth transitions

### Future Ideas 💡
- More classic arcade games (Pac-Man, Space Invaders, etc.)
- Sound effects using terminal beep
- Game state saving/loading
- Tournament mode with timed competitions
- Network multiplayer support
- Animation effects and transitions
- Theme sharing community with public theme repository