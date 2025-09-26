package org.example.arcade

import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Core arcade system managing games, scores, and terminal I/O
 */
class ArcadeSystem {
    private val terminal: Terminal = TerminalBuilder.builder()
        .system(true)
        .build()
    
    private val scoreManager = ScoreManager()
    private val cartridges = mutableListOf<GameCartridge>()
    
    init {
        // Set up terminal for raw input
        terminal.enterRawMode()
    }
    
    fun addCartridge(cartridge: GameCartridge) {
        cartridges.add(cartridge)
    }
    
    fun getScoreManager(): ScoreManager = scoreManager
    
    fun getTerminal(): Terminal = terminal
    
    /**
     * Get input character without blocking indefinitely
     */
    fun getInput(timeoutMs: Long = 50): Int? {
        return try {
            if (terminal.reader().ready()) {
                terminal.reader().read()
            } else {
                Thread.sleep(timeoutMs)
                if (terminal.reader().ready()) {
                    terminal.reader().read()
                } else null
            }
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * Clear the screen with retro style
     */
    fun clearScreen() {
        terminal.writer().print("\u001B[2J\u001B[H")
        terminal.writer().flush()
    }
    
    /**
     * Print at specific position with color
     */
    fun printAt(x: Int, y: Int, text: String, color: String = "") {
        terminal.writer().print("\u001B[${y};${x}H$color$text\u001B[0m")
        terminal.writer().flush()
    }
    
    /**
     * Get current date/time string for scores
     */
    fun getCurrentDateTime(): String {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
    }
    
    /**
     * Show main arcade menu
     */
    suspend fun showMainMenu() {
        var selectedGame = 0
        var running = true
        
        while (running) {
            clearScreen()
            showArcadeLogo()
            showGameList(selectedGame)
            showControls()
            
            when (getInput(100)) {
                27 -> { // ESC key
                    val next1 = getInput(10)
                    val next2 = getInput(10)
                    if (next1 == 91) { // Arrow key sequence
                        when (next2) {
                            65 -> selectedGame = (selectedGame - 1 + cartridges.size) % cartridges.size // Up
                            66 -> selectedGame = (selectedGame + 1) % cartridges.size // Down
                        }
                    }
                }
                13, 10 -> { // Enter key
                    if (cartridges.isNotEmpty()) {
                        cartridges[selectedGame].play(this)
                    }
                }
                113 -> running = false // 'q' key
                115 -> { // 's' key - show scores
                    showHighScores()
                }
            }
            
            delay(50)
        }
    }
    
    private fun showArcadeLogo() {
        val logo = """
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
        
        printAt(1, 1, logo, "\u001B[36m") // Cyan color
    }
    
    private fun showGameList(selected: Int) {
        printAt(1, 15, "═══ AVAILABLE CARTRIDGES ═══", "\u001B[33m") // Yellow
        
        if (cartridges.isEmpty()) {
            printAt(1, 17, "No cartridges loaded!", "\u001B[31m") // Red
            return
        }
        
        cartridges.forEachIndexed { index, cartridge ->
            val prefix = if (index == selected) "► " else "  "
            val color = if (index == selected) "\u001B[32m" else "\u001B[37m" // Green for selected, white for others
            
            printAt(1, 17 + index, "$prefix${cartridge.icon} ${cartridge.name}", color)
            printAt(30, 17 + index, cartridge.description, "\u001B[90m") // Gray
        }
    }
    
    private fun showControls() {
        val controlY = 17 + cartridges.size + 2
        printAt(1, controlY, "═══ CONTROLS ═══", "\u001B[33m")
        printAt(1, controlY + 1, "↑↓ Navigate   ENTER Play   S Scores   Q Quit", "\u001B[37m")
    }
    
    private suspend fun showHighScores() {
        clearScreen()
        printAt(1, 1, "═══ HIGH SCORES ═══", "\u001B[33m")
        
        var y = 3
        cartridges.forEach { cartridge ->
            printAt(1, y, "${cartridge.name}:", "\u001B[32m")
            y++
            
            val scores = cartridge.getHighScores(scoreManager)
            if (scores.isEmpty()) {
                printAt(3, y, "No scores yet!", "\u001B[90m")
                y++
            } else {
                scores.forEachIndexed { index, score ->
                    printAt(3, y, "${index + 1}. ${score.playerName} - ${score.score} pts (Level ${score.level})", "\u001B[37m")
                    y++
                }
            }
            y++
        }
        
        printAt(1, y + 2, "Press any key to return...", "\u001B[90m")
        getInput(10000) // Wait for input
    }
    
    fun cleanup() {
        try {
            terminal.close()
        } catch (e: Exception) {
            // Ignore cleanup errors
        }
    }
}