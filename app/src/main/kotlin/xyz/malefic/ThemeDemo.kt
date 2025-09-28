package xyz.malefic

import com.googlecode.lanterna.terminal.DefaultTerminalFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import xyz.malefic.arcade.theme.ThemeManager

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
        println("Demonstrating all themes with Lanterna...")

        val terminal = DefaultTerminalFactory().createTerminal()
        terminal.enterPrivateMode()

        runBlocking {
            themeManager.getAllThemes().forEach { theme ->
                println("\n--- Theme: ${theme.name} ---")
                themeManager.setCurrentTheme(theme)

                terminal.clearScreen()
                terminal.setCursorPosition(0, 0)

                val lanternaColors = theme.toLanternaColors()

                terminal.setForegroundColor(lanternaColors.primary)
                terminal.putString("Primary: ArcadeTUI Logo\n")

                terminal.setForegroundColor(lanternaColors.secondary)
                terminal.putString("Secondary: Menu headers\n")

                terminal.setForegroundColor(lanternaColors.accent)
                terminal.putString("Accent: Selected items\n")

                terminal.setForegroundColor(lanternaColors.text)
                terminal.putString("Text: Normal text\n")

                terminal.setForegroundColor(lanternaColors.textDim)
                terminal.putString("TextDim: Subtle information\n")

                terminal.setForegroundColor(lanternaColors.success)
                terminal.putString("Success: Positive messages\n")

                terminal.setForegroundColor(lanternaColors.warning)
                terminal.putString("Warning: Caution messages\n")

                terminal.setForegroundColor(lanternaColors.error)
                terminal.putString("Error: Error messages\n")

                terminal.setForegroundColor(lanternaColors.player)
                terminal.putString("Player: Player elements\n")

                terminal.setForegroundColor(lanternaColors.enemy)
                terminal.putString("Enemy: Enemy elements\n")

                terminal.setForegroundColor(lanternaColors.bullet)
                terminal.putString("Bullet: Projectiles\n")

                terminal.setForegroundColor(lanternaColors.border)
                terminal.putString("Border: UI borders\n")

                terminal.flush()
                delay(2000) // Pause between themes
            }
        }

        terminal.exitPrivateMode()
        terminal.close()
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
    println("Use --demo to see themes in action with Lanterna")
}
