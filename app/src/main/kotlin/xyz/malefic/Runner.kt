package xyz.malefic

import kotlinx.coroutines.delay
import xyz.malefic.arcade.ArcadeSystem
import xyz.malefic.games.GalagaCartridge
import xyz.malefic.games.TetrisCartridge

/**
 * Main entry point for ArcadeTUI - A retro terminal arcade system
 */
suspend fun main(args: Array<String>) {
    val arcade = ArcadeSystem()

    try {
        println("🕹️  Starting ArcadeTUI...")

        // Load game cartridges
        arcade.addCartridge(TetrisCartridge())
        arcade.addCartridge(GalagaCartridge())

        println("✅ Game cartridges loaded!")
        println("🎮 Use arrow keys to navigate, ENTER to play, Q to quit")
        println("📊 Press S to view high scores")
        println("🎨 Current theme: ${arcade.getThemeManager().currentTheme.name}")
        println()
        println("Starting in 3 seconds...")
        delay(3000)

        // Start the arcade system
        arcade.showMainMenu()
    } catch (e: Exception) {
        println("❌ Error running arcade: ${e.message}")
        e.printStackTrace()
    } finally {
        arcade.cleanup()
        println("\n👋 Thanks for playing ArcadeTUI!")
    }
}
