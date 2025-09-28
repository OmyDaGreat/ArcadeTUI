package xyz.malefic.arcade

import com.googlecode.lanterna.TextColor
import com.googlecode.lanterna.input.KeyType
import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import com.googlecode.lanterna.terminal.Terminal
import kotlinx.coroutines.delay
import xyz.malefic.arcade.theme.ThemeManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Core arcade system managing games, scores, and terminal I/O using Lanterna
 */
class ArcadeSystem {
    private val scoreManager = ScoreManager()
    private val cartridges = mutableListOf<GameCartridge>()
    private val themeManager = ThemeManager()
    private var terminal: Terminal? = null

    fun addCartridge(cartridge: GameCartridge) {
        cartridges.add(cartridge)
    }

    fun getScoreManager(): ScoreManager = scoreManager

    fun getThemeManager(): ThemeManager = themeManager

    /**
     * Get current date/time string for scores
     */
    fun getCurrentDateTime(): String = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))

    /**
     * Initialize terminal for Lanterna
     */
    private fun initializeTerminal(): Terminal {
        if (terminal == null) {
            terminal = DefaultTerminalFactory().createTerminal()
            terminal?.enterPrivateMode()
            terminal?.clearScreen()
        }
        return terminal!!
    }

    /**
     * Show interactive main arcade menu using Lanterna
     */
    suspend fun showMainMenu() {
        val term = initializeTerminal()
        var selectedIndex = 0
        val menuItems = mutableListOf<MenuItem>()

        // Add game cartridges to menu
        cartridges.forEach { cartridge ->
            menuItems.add(MenuItem.Game(cartridge))
        }

        // Add other menu options
        menuItems.add(MenuItem.HighScores)
        menuItems.add(MenuItem.ThemeSelector)
        menuItems.add(MenuItem.Quit)

        var running = true
        while (running) {
            // Clear screen and display menu using Lanterna
            term.clearScreen()
            term.setCursorPosition(0, 0)

            val theme = themeManager.currentTheme.toLanternaColors()

            // Show logo
            printWithColor(term, showArcadeLogo(), theme.primary)
            term.putCharacter('\n')

            // Show menu items
            term.setForegroundColor(theme.secondary)
            term.putString("═══ MAIN MENU ═══\n")
            term.putCharacter('\n')

            menuItems.forEachIndexed { index, item ->
                val isSelected = index == selectedIndex
                if (isSelected) {
                    term.setForegroundColor(theme.accent)
                    term.putString("► ${item.displayName}")
                } else {
                    term.setForegroundColor(theme.text)
                    term.putString("  ${item.displayName}")
                }
                term.setForegroundColor(theme.textDim)
                term.putString("  ${item.description}\n")
            }

            term.putCharacter('\n')
            term.setForegroundColor(theme.textDim)
            term.putString("Use ↑↓ (W/S) to navigate, ENTER to select, Q to quit\n")
            term.putString("Current theme: ${themeManager.currentTheme.name}\n")

            term.flush()

            // Handle input
            val keyStroke = term.pollInput()
            keyStroke?.let { key ->
                when {
                    key.keyType == KeyType.ArrowUp || key.character?.lowercaseChar() == 'w' -> {
                        selectedIndex = if (selectedIndex > 0) selectedIndex - 1 else menuItems.size - 1
                    }
                    key.keyType == KeyType.ArrowDown || key.character?.lowercaseChar() == 's' -> {
                        selectedIndex = if (selectedIndex < menuItems.size - 1) selectedIndex + 1 else 0
                    }
                    key.keyType == KeyType.Enter -> {
                        when (val item = menuItems[selectedIndex]) {
                            is MenuItem.Game -> {
                                // Launch the game
                                launchGame(item.cartridge)
                            }
                            MenuItem.HighScores -> {
                                showHighScores()
                            }
                            MenuItem.ThemeSelector -> {
                                showThemeSelector()
                            }
                            MenuItem.Quit -> {
                                running = false
                            }
                        }
                    }
                    key.character?.lowercaseChar() == 'q' -> {
                        running = false
                    }
                }
            }

            delay(50) // Small delay to prevent busy waiting
        }
    }

    /**
     * Helper function to print text with color using Lanterna
     */
    private fun printWithColor(
        terminal: Terminal,
        text: String,
        color: TextColor,
    ) {
        terminal.setForegroundColor(color)
        text.lines().forEachIndexed { i, line ->
            terminal.putString(line)
            if (i < text.lines().size - 1) {
                val pos = terminal.cursorPosition
                terminal.setCursorPosition(0, pos.row + 1)
            }
        }
    }

    /**
     * Launch a game cartridge
     */
    private suspend fun launchGame(cartridge: GameCartridge) {
        try {
            cartridge.play(this)
        } catch (e: Exception) {
            showError("Error launching game: ${e.message}")
        }
    }

    /**
     * Show high scores menu
     */
    private suspend fun showHighScores() {
        val term = terminal!!
        var running = true
        while (running) {
            term.clearScreen()
            term.setCursorPosition(0, 0)

            val theme = themeManager.currentTheme.toLanternaColors()

            term.setForegroundColor(theme.primary)
            term.putString("═══ HIGH SCORES ═══\n\n")

            if (cartridges.isEmpty()) {
                term.setForegroundColor(theme.textDim)
                term.putString("No games available\n")
            } else {
                cartridges.forEach { cartridge ->
                    term.setForegroundColor(theme.secondary)
                    term.putString("${cartridge.name}:\n")
                    val scores = scoreManager.loadScores(cartridge.name.lowercase())
                    if (scores.isEmpty()) {
                        term.setForegroundColor(theme.textDim)
                        term.putString("  No scores yet\n")
                    } else {
                        scores.take(5).forEach { score ->
                            term.setForegroundColor(theme.text)
                            term.putString("  ${score.playerName}: ${score.score} (${score.date})\n")
                        }
                    }
                    term.putCharacter('\n')
                }
            }

            term.setForegroundColor(theme.textDim)
            term.putString("Press ESC or Q to return to main menu\n")
            term.flush()

            val keyStroke = term.pollInput()
            keyStroke?.let { key ->
                when {
                    key.keyType == KeyType.Escape || key.character?.lowercaseChar() == 'q' -> {
                        running = false
                    }
                }
            }

            delay(50)
        }
    }

    /**
     * Show theme selector menu
     */
    private suspend fun showThemeSelector() {
        val term = terminal!!
        var selectedThemeIndex =
            themeManager
                .getAllThemes()
                .indexOfFirst {
                    it.name == themeManager.currentTheme.name
                }.takeIf { it >= 0 } ?: 0

        val themes = themeManager.getAllThemes()
        var running = true

        while (running) {
            term.clearScreen()
            term.setCursorPosition(0, 0)

            val currentTheme = themes[selectedThemeIndex]
            val lanternaColors = currentTheme.toLanternaColors()

            term.setForegroundColor(lanternaColors.primary)
            term.putString("═══ THEME SELECTOR ═══\n\n")

            themes.forEachIndexed { index, theme ->
                val isSelected = index == selectedThemeIndex
                val isActive = theme.name == themeManager.currentTheme.name

                when {
                    isSelected && isActive -> {
                        term.setForegroundColor(lanternaColors.success)
                        term.putString("► ${theme.name} (ACTIVE)")
                    }
                    isSelected -> {
                        term.setForegroundColor(lanternaColors.accent)
                        term.putString("► ${theme.name}")
                    }
                    isActive -> {
                        term.setForegroundColor(lanternaColors.success)
                        term.putString("  ${theme.name} (ACTIVE)")
                    }
                    else -> {
                        term.setForegroundColor(lanternaColors.text)
                        term.putString("  ${theme.name}")
                    }
                }
                term.setForegroundColor(lanternaColors.textDim)
                term.putString(" - ${theme.description}\n")
            }

            term.putCharacter('\n')
            term.setForegroundColor(lanternaColors.secondary)
            term.putString("═══ PREVIEW ═══\n")

            term.setForegroundColor(lanternaColors.primary)
            term.putString("Primary ")
            term.setForegroundColor(lanternaColors.secondary)
            term.putString("Secondary ")
            term.setForegroundColor(lanternaColors.accent)
            term.putString("Accent ")
            term.setForegroundColor(lanternaColors.success)
            term.putString("Success ")
            term.setForegroundColor(lanternaColors.warning)
            term.putString("Warning ")
            term.setForegroundColor(lanternaColors.error)
            term.putString("Error\n")

            term.putCharacter('\n')
            term.setForegroundColor(lanternaColors.textDim)
            term.putString("Use ↑↓ (W/S) to navigate, ENTER to apply theme, ESC/Q to return\n")
            term.flush()

            val keyStroke = term.pollInput()
            keyStroke?.let { key ->
                when {
                    key.keyType == KeyType.ArrowUp || key.character?.lowercaseChar() == 'w' -> {
                        selectedThemeIndex = if (selectedThemeIndex > 0) selectedThemeIndex - 1 else themes.size - 1
                    }
                    key.keyType == KeyType.ArrowDown || key.character?.lowercaseChar() == 's' -> {
                        selectedThemeIndex = if (selectedThemeIndex < themes.size - 1) selectedThemeIndex + 1 else 0
                    }
                    key.keyType == KeyType.Enter -> {
                        themeManager.setCurrentTheme(themes[selectedThemeIndex])
                        // Show confirmation briefly
                        showMessage("Theme changed to: ${themes[selectedThemeIndex].name}", 1000)
                    }
                    key.keyType == KeyType.Escape || key.character?.lowercaseChar() == 'q' -> {
                        running = false
                    }
                }
            }

            delay(50)
        }
    }

    /**
     * Show error message
     */
    private suspend fun showError(message: String) {
        val term = terminal!!
        var running = true
        while (running) {
            term.clearScreen()
            term.setCursorPosition(0, 0)

            val theme = themeManager.currentTheme.toLanternaColors()

            term.setForegroundColor(theme.error)
            term.putString("ERROR\n\n")

            term.setForegroundColor(theme.text)
            term.putString("${message}\n\n")

            term.setForegroundColor(theme.textDim)
            term.putString("Press ENTER or ESC to continue\n")
            term.flush()

            val keyStroke = term.pollInput()
            keyStroke?.let { key ->
                when {
                    key.keyType == KeyType.Enter || key.keyType == KeyType.Escape -> {
                        running = false
                    }
                }
            }

            delay(50)
        }
    }

    /**
     * Show temporary message
     */
    private suspend fun showMessage(
        message: String,
        durationMs: Long,
    ) {
        val term = terminal!!
        val startTime = System.currentTimeMillis()

        term.clearScreen()
        term.setCursorPosition(0, 0)

        val theme = themeManager.currentTheme.toLanternaColors()

        term.setForegroundColor(theme.success)
        term.putString("${message}\n\n")

        term.setForegroundColor(theme.textDim)
        term.putString("This message will disappear in ${(durationMs - (System.currentTimeMillis() - startTime)) / 1000 + 1} seconds...\n")
        term.flush()

        delay(durationMs)
    }

    /**
     * Menu item types for navigation
     */
    private sealed class MenuItem(
        val displayName: String,
        val description: String,
    ) {
        data class Game(
            val cartridge: GameCartridge,
        ) : MenuItem(
                "${cartridge.icon} ${cartridge.name}",
                cartridge.description,
            )

        object HighScores : MenuItem("📊 High Scores", "View top scores for all games")

        object ThemeSelector : MenuItem("🎨 Themes", "Change visual theme")

        object Quit : MenuItem("❌ Quit", "Exit ArcadeTUI")
    }

    /**
     * Cycle through themes demo
     */
    suspend fun cycleThemesDemo() {
        val term = initializeTerminal()
        val themes = themeManager.getAllThemes()
        themes.forEach { theme ->
            themeManager.setCurrentTheme(theme)

            term.clearScreen()
            term.setCursorPosition(0, 0)

            val lanternaColors = theme.toLanternaColors()

            term.setForegroundColor(lanternaColors.primary)
            term.putString("╔══════════════════════════════════════╗\n")
            term.putString("║          THEME: ${theme.name.uppercase().padEnd(24)} ║\n")
            term.putString("╚══════════════════════════════════════╝\n")

            term.setForegroundColor(lanternaColors.secondary)
            term.putString("Description: ${theme.description}\n\n")

            term.setForegroundColor(lanternaColors.text)
            term.putString("Color preview:\n")

            term.setForegroundColor(lanternaColors.primary)
            term.putString("Primary ")
            term.setForegroundColor(lanternaColors.secondary)
            term.putString("Secondary ")
            term.setForegroundColor(lanternaColors.accent)
            term.putString("Accent ")
            term.setForegroundColor(lanternaColors.success)
            term.putString("Success ")
            term.setForegroundColor(lanternaColors.warning)
            term.putString("Warning ")
            term.setForegroundColor(lanternaColors.error)
            term.putString("Error\n")

            term.putCharacter('\n')
            term.setForegroundColor(lanternaColors.textDim)
            term.putString("Press any key for next theme...\n")
            term.flush()

            // Wait for input or timeout
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 3000) {
                val keyStroke = term.pollInput()
                if (keyStroke != null) {
                    break
                }
                delay(50)
            }
        }

        println("Theme cycling complete!")
    }

    private fun showArcadeLogo(): String =
        """
 /$$      /$$           /$$            /$$$$$$  /$$
| $$$    /$$$          | $$           /$${'$'}__  $$|__/
| $$$$  /$$$$  /$$$$$$ | $$  /$$$$$$ | $$  \__/ /$$  /$$$$$$$
| $$ $$/$$ $$ |____  $$| $$ /$${'$'}__  $$| $$$$    | $$ /$${'$'}_____/
| $$  $$$| $$  /$$$$$$$| $$| $$$$$$$$| $${'$'}_/    | $$| $$
| $$\  $ | $$ /$${'$'}__  $$| $$| $${'$'}_____/| $$      | $$| $$
| $$ \/  | $$|  $$$$$$$| $$|  $$$$$$$| $$      | $$|  $$$$$$$
|__/     |__/ \_______/|__/ \_______/|__/      |__/ \_______/
        """.trimIndent()

    /**
     * Legacy methods for backward compatibility during migration
     */
    fun clearScreen() {
        println("\u001B[2J\u001B[H")
    }

    fun printAt(
        x: Int,
        y: Int,
        text: String,
        color: String = "",
    ) {
        print("\u001B[$y;${x}H$color$text\u001B[0m")
    }

    fun getInput(timeoutMs: Long = 50): Int? {
        // For backward compatibility with games during migration
        return System.`in`.let { input ->
            if (input.available() > 0) {
                input.read()
            } else {
                Thread.sleep(timeoutMs)
                if (input.available() > 0) input.read() else null
            }
        }
    }

    fun cleanup() {
        terminal?.let { term ->
            term.clearScreen()
            term.exitPrivateMode()
            term.close()
        }
    }
}
