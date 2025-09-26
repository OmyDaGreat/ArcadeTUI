package org.example.arcade

import com.varabyte.kotter.foundation.session
import com.varabyte.kotter.foundation.text.color
import com.varabyte.kotter.foundation.text.text
import com.varabyte.kotter.foundation.text.textLine
import kotlinx.coroutines.delay
import org.example.arcade.theme.ThemeManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Core arcade system managing games, scores, and terminal I/O using Kotter
 */
class ArcadeSystem {
    private val scoreManager = ScoreManager()
    private val cartridges = mutableListOf<GameCartridge>()
    private val themeManager = ThemeManager()

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
     * Show interactive main arcade menu using Kotter
     */
    suspend fun showMainMenu() {
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
            // Display menu using Kotter session
            session {
                section {
                    val theme = themeManager.currentTheme.toKotterColors()
                    
                    // Show logo
                    color(theme.primary) {
                        textLine(showArcadeLogo())
                    }
                    textLine()
                    
                    // Show menu items
                    color(theme.secondary) { textLine("═══ MAIN MENU ═══") }
                    textLine()
                    
                    menuItems.forEachIndexed { index, item ->
                        val isSelected = index == selectedIndex
                        if (isSelected) {
                            color(theme.accent) { 
                                text("► ${item.displayName}")
                            }
                        } else {
                            color(theme.text) { 
                                text("  ${item.displayName}")
                            }
                        }
                        color(theme.textDim) { textLine("  ${item.description}") }
                    }
                    
                    textLine()
                    color(theme.textDim) { textLine("Use ↑↓ (W/S) to navigate, ENTER to select, Q to quit") }
                    color(theme.textDim) { textLine("Current theme: ${themeManager.currentTheme.name}") }
                }
            }
            
            // Handle input
            val input = getInput(100)
            when (input) {
                // Arrow keys or WASD for navigation
                65, 119, 87 -> { // Up arrow, w, W
                    selectedIndex = if (selectedIndex > 0) selectedIndex - 1 else menuItems.size - 1
                }
                66, 115, 83 -> { // Down arrow, s, S
                    selectedIndex = if (selectedIndex < menuItems.size - 1) selectedIndex + 1 else 0
                }
                10, 13 -> { // Enter
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
                113, 81 -> { // q, Q
                    running = false
                }
            }
            
            delay(50) // Small delay to prevent excessive CPU usage
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
        var running = true
        while (running) {
            session {
                section {
                    val theme = themeManager.currentTheme.toKotterColors()
                    
                    color(theme.primary) { textLine("═══ HIGH SCORES ═══") }
                    textLine()
                    
                    if (cartridges.isEmpty()) {
                        color(theme.textDim) { textLine("No games available") }
                    } else {
                        cartridges.forEach { cartridge ->
                            color(theme.secondary) { textLine("${cartridge.name}:") }
                            val scores = scoreManager.loadScores(cartridge.name.lowercase())
                            if (scores.isEmpty()) {
                                color(theme.textDim) { textLine("  No scores yet") }
                            } else {
                                scores.take(5).forEach { score ->
                                    color(theme.text) { 
                                        textLine("  ${score.playerName}: ${score.score} (${score.date})")
                                    }
                                }
                            }
                            textLine()
                        }
                    }
                    
                    color(theme.textDim) { textLine("Press ESC or Q to return to main menu") }
                }
            }
            
            val input = getInput(100)
            when (input) {
                27, 113, 81 -> { // ESC, q, Q
                    running = false
                }
            }
            
            delay(50)
        }
    }
    
    /**
     * Show theme selector menu
     */
    private suspend fun showThemeSelector() {
        var selectedThemeIndex = themeManager.getAllThemes().indexOfFirst { 
            it.name == themeManager.currentTheme.name 
        }.takeIf { it >= 0 } ?: 0
        
        val themes = themeManager.getAllThemes()
        var running = true
        
        while (running) {
            session {
                section {
                    val currentTheme = themes[selectedThemeIndex]
                    val kotterColors = currentTheme.toKotterColors()
                    
                    color(kotterColors.primary) { textLine("═══ THEME SELECTOR ═══") }
                    textLine()
                    
                    themes.forEachIndexed { index, theme ->
                        val isSelected = index == selectedThemeIndex
                        val isActive = theme.name == themeManager.currentTheme.name
                        
                        when {
                            isSelected && isActive -> {
                                color(kotterColors.success) { text("► ${theme.name} (ACTIVE)") }
                            }
                            isSelected -> {
                                color(kotterColors.accent) { text("► ${theme.name}") }
                            }
                            isActive -> {
                                color(kotterColors.success) { text("  ${theme.name} (ACTIVE)") }
                            }
                            else -> {
                                color(kotterColors.text) { text("  ${theme.name}") }
                            }
                        }
                        color(kotterColors.textDim) { textLine(" - ${theme.description}") }
                    }
                    
                    textLine()
                    color(kotterColors.secondary) { textLine("═══ PREVIEW ═══") }
                    color(kotterColors.primary) { text("Primary ") }
                    color(kotterColors.secondary) { text("Secondary ") }
                    color(kotterColors.accent) { text("Accent ") }
                    color(kotterColors.success) { text("Success ") }
                    color(kotterColors.warning) { text("Warning ") }
                    color(kotterColors.error) { textLine("Error") }
                    
                    textLine()
                    color(kotterColors.textDim) { textLine("Use ↑↓ (W/S) to navigate, ENTER to apply theme, ESC/Q to return") }
                }
            }
            
            val input = getInput(100)
            when (input) {
                65, 119, 87 -> { // Up arrow, w, W
                    selectedThemeIndex = if (selectedThemeIndex > 0) selectedThemeIndex - 1 else themes.size - 1
                }
                66, 115, 83 -> { // Down arrow, s, S
                    selectedThemeIndex = if (selectedThemeIndex < themes.size - 1) selectedThemeIndex + 1 else 0
                }
                10, 13 -> { // Enter
                    themeManager.setCurrentTheme(themes[selectedThemeIndex])
                    // Show confirmation briefly
                    showMessage("Theme changed to: ${themes[selectedThemeIndex].name}", 1000)
                }
                27, 113, 81 -> { // ESC, q, Q
                    running = false
                }
            }
            
            delay(50)
        }
    }
    
    /**
     * Show error message
     */
    private suspend fun showError(message: String) {
        var running = true
        while (running) {
            session {
                section {
                    val theme = themeManager.currentTheme.toKotterColors()
                    
                    color(theme.error) { textLine("ERROR") }
                    textLine()
                    color(theme.text) { textLine(message) }
                    textLine()
                    color(theme.textDim) { textLine("Press ENTER or ESC to continue") }
                }
            }
            
            val input = getInput(100)
            when (input) {
                10, 13, 27 -> { // Enter, ESC
                    running = false
                }
            }
            
            delay(50)
        }
    }
    
    /**
     * Show temporary message
     */
    private suspend fun showMessage(message: String, durationMs: Long) {
        val startTime = System.currentTimeMillis()
        
        session {
            section {
                val theme = themeManager.currentTheme.toKotterColors()
                
                color(theme.success) { textLine(message) }
                textLine()
                color(theme.textDim) { textLine("This message will disappear in ${(durationMs - (System.currentTimeMillis() - startTime)) / 1000 + 1} seconds...") }
            }
        }
        
        delay(durationMs)
    }
    
    /**
     * Menu item types for navigation
     */
    private sealed class MenuItem(val displayName: String, val description: String) {
        data class Game(val cartridge: GameCartridge) : MenuItem(
            "${cartridge.icon} ${cartridge.name}",
            cartridge.description
        )
        
        object HighScores : MenuItem("📊 High Scores", "View top scores for all games")
        object ThemeSelector : MenuItem("🎨 Themes", "Change visual theme")
        object Quit : MenuItem("❌ Quit", "Exit ArcadeTUI")
    }

    /**
     * Cycle through themes demo
     */
    suspend fun cycleThemesDemo() {
        val themes = themeManager.getAllThemes()
        themes.forEach { theme ->
            themeManager.setCurrentTheme(theme)

            session {
                section {
                    val kotterColors = theme.toKotterColors()

                    color(kotterColors.primary) {
                        textLine("╔══════════════════════════════════════╗")
                        textLine("║          THEME: ${theme.name.uppercase().padEnd(24)} ║")
                        textLine("╚══════════════════════════════════════╝")
                    }

                    color(kotterColors.secondary) { textLine("Description: ${theme.description}") }
                    textLine()

                    color(kotterColors.text) { textLine("Color preview:") }
                    color(kotterColors.primary) { text("Primary ") }
                    color(kotterColors.secondary) { text("Secondary ") }
                    color(kotterColors.accent) { text("Accent ") }
                    color(kotterColors.success) { text("Success ") }
                    color(kotterColors.warning) { text("Warning ") }
                    color(kotterColors.error) { textLine("Error") }

                    textLine()
                    color(kotterColors.textDim) { textLine("Press any key for next theme...") }
                }
            }

            // Wait for input or timeout
            val startTime = System.currentTimeMillis()
            while (System.currentTimeMillis() - startTime < 3000) {
                if (System.`in`.available() > 0) {
                    System.`in`.read()
                    break
                }
                delay(50)
            }
        }

        println("Theme cycling complete!")
    }

    private fun showArcadeLogo(): String =
        """
╔═══════════════════════════════════════════════════════════╗
║      ▄▄▄     ██▀███    ██▀████▄  ▄██████▄   ███████▄      ║
║     ▀█████   ██   ██   ██   ████ ██▄▄▄▄▄██  ████▀▀███     ║
║    ██▀ ▀██   ██▀██▀    ██    ██  ██▀▀▀▀███  ██▀    ██     ║
║    ██   ██   ██  ██▄   ██▄▄███   ███▄▄▄███  ██████████    ║
║    ▀█████▀   ██   ▀██  ▀██████▀   ▀▀████▀▀  ███████▀      ║
║                                                           ║
║                   T U I   A R C A D E                     ║
║                                                           ║
║                🕹️  Insert Coin to Play  🕹️                  ║
╚═══════════════════════════════════════════════════════════╝
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
        // Kotter handles cleanup automatically
    }
}
