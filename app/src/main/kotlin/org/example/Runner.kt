package org.example

import org.example.arcade.ArcadeSystem
import org.example.games.TetrisCartridge
import org.example.games.GalagaCartridge
import kotlinx.coroutines.runBlocking

/**
 * Main entry point for ArcadeTUI - A retro terminal arcade system
 */
fun main(args: Array<String>) {
    val arcade = ArcadeSystem()
    
    try {
        // Handle special command line arguments
        when {
            args.contains("--cycle-themes") -> {
                println("🎨 Cycling through all available themes...")
                runBlocking {
                    arcade.cycleThemesDemo()
                }
                return
            }
            args.contains("--theme-demo") -> {
                println("🎨 Theme Demo Mode")
                println("Available themes: ${arcade.getThemeManager().getAllThemes().size}")
                arcade.getThemeManager().getAllThemes().forEach { theme ->
                    println("- ${theme.name}: ${theme.description}")
                }
                return
            }
        }
        
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
        Thread.sleep(3000)
        
        // Start the arcade system
        runBlocking {
            arcade.showMainMenu()
        }
        
    } catch (e: Exception) {
        println("❌ Error running arcade: ${e.message}")
        e.printStackTrace()
    } finally {
        arcade.cleanup()
        println("\n👋 Thanks for playing ArcadeTUI!")
    }
}
