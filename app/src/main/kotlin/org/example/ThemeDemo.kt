package org.example

import org.example.arcade.theme.ThemeManager
import com.varabyte.kotter.foundation.*
import com.varabyte.kotter.foundation.text.*
import kotlinx.coroutines.runBlocking

/**
 * Theme demo utility to showcase the theming system
 */
fun main(args: Array<String>) {
    println("🎨 ArcadeTUI Theme System Demo")
    println("==============================")
    
    val themeManager = ThemeManager()
    
    if (args.isNotEmpty() && args[0] == "--list") {
        println("Available themes:")
        themeManager.getAllThemes().forEach { theme ->
            println("- ${theme.name}: ${theme.description}")
        }
        return
    }
    
    if (args.isNotEmpty() && args[0] == "--demo") {
        println("Demonstrating all themes with Kotter...")
        
        themeManager.getAllThemes().forEach { theme ->
            println("\n--- Theme: ${theme.name} ---")
            themeManager.setCurrentTheme(theme)
            
            runBlocking {
                session {
                    section {
                        val kotterColors = theme.toKotterColors()
                        
                        color(kotterColors.primary) { textLine("Primary: ArcadeTUI Logo") }
                        color(kotterColors.secondary) { textLine("Secondary: Menu headers") }
                        color(kotterColors.accent) { textLine("Accent: Selected items") }
                        color(kotterColors.text) { textLine("Text: Normal text") }
                        color(kotterColors.textDim) { textLine("TextDim: Subtle information") }
                        color(kotterColors.success) { textLine("Success: Positive messages") }
                        color(kotterColors.warning) { textLine("Warning: Caution messages") }
                        color(kotterColors.error) { textLine("Error: Error messages") }
                        color(kotterColors.player) { textLine("Player: Player elements") }
                        color(kotterColors.enemy) { textLine("Enemy: Enemy elements") }
                        color(kotterColors.bullet) { textLine("Bullet: Projectiles") }
                        color(kotterColors.border) { textLine("Border: UI borders") }
                    }
                }
            }
            
            Thread.sleep(2000) // Pause between themes
        }
        return
    }
    
    // Default: show available themes and their colors
    themeManager.getAllThemes().forEach { theme ->
        println("\n=== ${theme.name.uppercase()} ===")
        println("Description: ${theme.description}")
        println("Author: ${theme.author}")
        println("Colors:")
        println("  Primary: ${theme.colors.primary}")
        println("  Secondary: ${theme.colors.secondary}")
        println("  Accent: ${theme.colors.accent}")
        println("  Text: ${theme.colors.text}")
        println("  Success: ${theme.colors.success}")
        println("  Warning: ${theme.colors.warning}")
        println("  Error: ${theme.colors.error}")
    }
    
    println("\nUse --list to see just theme names")
    println("Use --demo to see themes in action with Kotter")
}