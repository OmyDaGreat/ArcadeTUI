package org.example.arcade

import com.varabyte.kotter.foundation.*
import com.varabyte.kotter.foundation.text.*
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
                val theme = themeManager.currentTheme.toKotterColors()
                
                // Show logo
                color(theme.primary) { 
                    textLine(showArcadeLogo())
                }
                textLine()
                
                // Show games
                color(theme.secondary) { textLine("═══ AVAILABLE CARTRIDGES ═══") }
                
                if (cartridges.isEmpty()) {
                    color(theme.error) { textLine("No cartridges loaded!") }
                } else {
                    cartridges.forEachIndexed { index, cartridge ->
                        color(theme.accent) { text("► " + cartridge.icon + " " + cartridge.name) }
                        color(theme.textDim) { textLine("  " + cartridge.description) }
                    }
                }
                
                textLine()
                color(theme.secondary) { textLine("═══ THEMING SYSTEM DEMO ═══") }
                color(theme.text) { textLine("Available themes:") }
                
                themeManager.getAllThemes().forEach { availableTheme ->
                    val isActive = availableTheme.name == themeManager.currentTheme.name
                    if (isActive) {
                        color(theme.success) { text("  ► ${availableTheme.name} (ACTIVE)") }
                    } else {
                        color(theme.textDim) { text("    ${availableTheme.name}") }
                    }
                    color(theme.textDim) { textLine(" - ${availableTheme.description}") }
                }
                
                textLine()
                color(theme.secondary) { textLine("═══ THEME COLORS PREVIEW ═══") }
                color(theme.primary) { text("Primary ") }
                color(theme.secondary) { text("Secondary ") }
                color(theme.accent) { text("Accent ") }
                color(theme.success) { text("Success ") }
                color(theme.warning) { text("Warning ") }
                color(theme.error) { textLine("Error") }
                
                color(theme.text) { text("Player ") }
                color(theme.enemy) { text("Enemy ") }
                color(theme.bullet) { text("Bullet ") }
                color(theme.border) { textLine("Border") }
                
                textLine()
                color(theme.textDim) { textLine("To cycle themes, run: gradle run --args=\"--cycle-themes\"") }
                color(theme.textDim) { textLine("To see theme demo, run: gradle run --args=\"--theme-demo\"") }
                color(theme.textDim) { textLine("Game launching will be implemented in the next phase...") }
            }
        }
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
                Thread.sleep(50)
            }
        }
        
        println("Theme cycling complete!")
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
     * Legacy methods for backward compatibility during migration
     */
    fun clearScreen() {
        println("\u001B[2J\u001B[H")
    }
    
    fun printAt(x: Int, y: Int, text: String, color: String = "") {
        print("\u001B[${y};${x}H$color$text\u001B[0m")
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