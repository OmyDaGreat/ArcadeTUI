package org.example.arcade

import com.varabyte.kotter.foundation.*
import com.varabyte.kotter.foundation.text.*
import com.varabyte.kotter.foundation.input.*
import org.example.arcade.theme.ThemeManager
import kotlinx.coroutines.*
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
    fun getCurrentDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
    
    /**
     * Show main arcade menu using Kotter
     */
    suspend fun showMainMenu() {
        session {
            section {
                var running = true
                var selectedIndex by liveVarOf(0)
                var currentScreen by liveVarOf("main")
                var selectedTheme by liveVarOf(0)
                
                onKeyPressed {
                    when (key) {
                        Keys.UP -> when (currentScreen) {
                            "main" -> selectedIndex = if (cartridges.isNotEmpty()) (selectedIndex - 1 + cartridges.size) % cartridges.size else 0
                            "themes" -> selectedTheme = if (themeManager.getAllThemes().isNotEmpty()) (selectedTheme - 1 + themeManager.getAllThemes().size) % themeManager.getAllThemes().size else 0
                        }
                        Keys.DOWN -> when (currentScreen) {
                            "main" -> selectedIndex = if (cartridges.isNotEmpty()) (selectedIndex + 1) % cartridges.size else 0
                            "themes" -> selectedTheme = if (themeManager.getAllThemes().isNotEmpty()) (selectedTheme + 1) % themeManager.getAllThemes().size else 0
                        }
                        Keys.ENTER -> when (currentScreen) {
                            "main" -> if (cartridges.isNotEmpty() && selectedIndex < cartridges.size) {
                                // This would need special handling for game launching
                                // For now we'll just show a message
                                textLine("Game launching not implemented in this version")
                            }
                            "themes" -> {
                                val themes = themeManager.getAllThemes()
                                if (themes.isNotEmpty() && selectedTheme < themes.size) {
                                    themeManager.setCurrentTheme(themes[selectedTheme])
                                }
                                currentScreen = "main"
                            }
                        }
                        Keys.ESC -> when (currentScreen) {
                            "scores", "themes" -> currentScreen = "main"
                            "main" -> running = false
                        }
                        Keys.Q -> running = false
                        Keys.S -> if (currentScreen == "main") currentScreen = "scores"
                        Keys.T -> if (currentScreen == "main") currentScreen = "themes"
                    }
                }
                
                while (running) {
                    val theme = themeManager.currentTheme.toKotterColors()
                    
                    when (currentScreen) {
                        "main" -> {
                            // Show logo
                            color(theme.primary) { 
                                text(showArcadeLogo())
                            }
                            textLine()
                            
                            // Show games
                            color(theme.secondary) { textLine("═══ AVAILABLE CARTRIDGES ═══") }
                            
                            if (cartridges.isEmpty()) {
                                color(theme.error) { textLine("No cartridges loaded!") }
                            } else {
                                cartridges.forEachIndexed { index, cartridge ->
                                    val prefix = if (index == selectedIndex) "► " else "  "
                                    if (index == selectedIndex) {
                                        color(theme.accent) { text(prefix + cartridge.icon + " " + cartridge.name) }
                                    } else {
                                        color(theme.text) { text(prefix + cartridge.icon + " " + cartridge.name) }
                                    }
                                    color(theme.textDim) { textLine("  " + cartridge.description) }
                                }
                            }
                            
                            textLine()
                            color(theme.secondary) { textLine("═══ CONTROLS ═══") }
                            color(theme.text) { textLine("↑↓ Navigate   ENTER Play   S Scores   T Themes   Q Quit") }
                            
                            textLine()
                            color(theme.textDim) { 
                                textLine("Current Theme: ${themeManager.currentTheme.name}")
                            }
                        }
                        
                        "scores" -> {
                            color(theme.secondary) { textLine("═══ HIGH SCORES ═══") }
                            textLine()
                            
                            cartridges.forEach { cartridge ->
                                color(theme.accent) { textLine("${cartridge.name}:") }
                                
                                val scores = cartridge.getHighScores(scoreManager)
                                if (scores.isEmpty()) {
                                    color(theme.textDim) { textLine("  No scores yet!") }
                                } else {
                                    scores.forEachIndexed { index, score ->
                                        color(theme.text) { 
                                            textLine("  ${index + 1}. ${score.playerName} - ${score.score} pts (Level ${score.level})")
                                        }
                                    }
                                }
                                textLine()
                            }
                            
                            color(theme.textDim) { textLine("Press ESC to return...") }
                        }
                        
                        "themes" -> {
                            color(theme.secondary) { textLine("═══ CHOOSE THEME ═══") }
                            textLine()
                            
                            val themes = themeManager.getAllThemes()
                            themes.forEachIndexed { index, themeItem ->
                                val prefix = if (index == selectedTheme) "► " else "  "
                                val isActive = themeItem.name == themeManager.currentTheme.name
                                
                                if (index == selectedTheme) {
                                    color(theme.accent) { text(prefix + themeItem.name) }
                                } else {
                                    color(theme.text) { text(prefix + themeItem.name) }
                                }
                                
                                if (isActive) {
                                    color(theme.success) { text(" (ACTIVE)") }
                                }
                                
                                textLine()
                                color(theme.textDim) { textLine("    " + themeItem.description) }
                            }
                            
                            textLine()
                            color(theme.textDim) { textLine("ENTER Select   ESC Back") }
                        }
                    }
                    
                    delay(16) // ~60 FPS refresh rate
                }
            }
        }
    }
    
    private fun showArcadeLogo(): String {
        return """
╔═══════════════════════════════════════════════════════════╗
║    ▄▄▄     ██▀███    ██▀████▄  ▄██████▄   ███████▄       ║
║   ▀█████   ██   ██   ██   ████ ██▄▄▄▄▄██  ████▀▀███      ║
║  ██▀ ▀██   ██▀██▀    ██    ██  ██▀▀▀▀███  ██▀    ██      ║
║  ██   ██   ██  ██▄   ██▄▄███   ███▄▄▄███  ██████████     ║
║  ▀█████▀   ██   ▀██  ▀██████▀   ▀▀████▀▀  ███████▀       ║
║                                                           ║
║                T U I   A R C A D E                        ║
║                                                           ║
║        🕹️  Insert Coin to Play  🕹️                      ║
╚═══════════════════════════════════════════════════════════╝
        """.trimIndent()
    }
    
    /**
     * Simple input method for games that need basic input (deprecated in Kotter-based implementation)
     */
    @Deprecated("Games should use Kotter's input handling directly")
    suspend fun getSimpleInput(timeoutMs: Long = 50): Char? {
        return null
    }
    
    /**
     * Legacy methods for games that haven't been converted to Kotter yet
     * These provide basic functionality but games should be updated to use Kotter directly
     */
    fun clearScreen() {
        println("\u001B[2J\u001B[H")
    }
    
    fun printAt(x: Int, y: Int, text: String, color: String = "") {
        print("\u001B[${y};${x}H$color$text\u001B[0m")
    }
    
    fun getInput(timeoutMs: Long = 50): Int? {
        // Simplified input for backward compatibility
        return null
    }
    
    fun cleanup() {
        // Kotter handles cleanup automatically
    }
}