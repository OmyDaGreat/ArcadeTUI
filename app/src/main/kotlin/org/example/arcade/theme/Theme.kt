package org.example.arcade.theme

import kotlinx.serialization.Serializable
import com.varabyte.kotter.foundation.text.Color

/**
 * Represents a theme configuration for the arcade system
 */
@Serializable
data class Theme(
    val name: String,
    val description: String,
    val version: String = "1.0",
    val author: String = "ArcadeTUI",
    val colors: ThemeColors,
    val styles: ThemeStyles = ThemeStyles()
) {
    fun toKotterColors(): KotterThemeColors {
        return KotterThemeColors(
            primary = parseHexColor(colors.primary) ?: Color.CYAN,
            secondary = parseHexColor(colors.secondary) ?: Color.YELLOW,
            accent = parseHexColor(colors.accent) ?: Color.GREEN,
            background = parseHexColor(colors.background) ?: Color.BLACK,
            text = parseHexColor(colors.text) ?: Color.WHITE,
            textDim = parseHexColor(colors.textDim) ?: Color.BRIGHT_BLACK,
            success = parseHexColor(colors.success) ?: Color.GREEN,
            warning = parseHexColor(colors.warning) ?: Color.YELLOW,
            error = parseHexColor(colors.error) ?: Color.RED,
            gameArea = parseHexColor(colors.gameArea) ?: Color.WHITE,
            player = parseHexColor(colors.player) ?: Color.YELLOW,
            enemy = parseHexColor(colors.enemy) ?: Color.RED,
            bullet = parseHexColor(colors.bullet) ?: Color.WHITE,
            border = parseHexColor(colors.border) ?: Color.WHITE
        )
    }
}

@Serializable
data class ThemeColors(
    val primary: String = "#00FFFF",      // Cyan
    val secondary: String = "#FFFF00",     // Yellow
    val accent: String = "#00FF00",        // Green
    val background: String = "#000000",    // Black
    val text: String = "#FFFFFF",          // White
    val textDim: String = "#808080",       // Gray
    val success: String = "#00FF00",       // Green
    val warning: String = "#FFFF00",       // Yellow
    val error: String = "#FF0000",         // Red
    val gameArea: String = "#FFFFFF",      // White
    val player: String = "#FFFF00",        // Yellow
    val enemy: String = "#FF0000",         // Red
    val bullet: String = "#FFFFFF",        // White
    val border: String = "#FFFFFF"         // White
)

@Serializable
data class ThemeStyles(
    val logoStyle: String = "bold",
    val menuStyle: String = "normal",
    val selectedStyle: String = "bold",
    val borderStyle: String = "normal"
)

/**
 * Kotter-compatible theme colors
 */
data class KotterThemeColors(
    val primary: Color,
    val secondary: Color,
    val accent: Color,
    val background: Color,
    val text: Color,
    val textDim: Color,
    val success: Color,
    val warning: Color,
    val error: Color,
    val gameArea: Color,
    val player: Color,
    val enemy: Color,
    val bullet: Color,
    val border: Color
)

/**
 * Extension function to parse hex color strings to Kotter Color
 * Uses predefined colors for now until we find the correct RGB constructor
 */
private fun parseHexColor(hex: String): Color? {
    return when (hex.uppercase()) {
        "#FF0000" -> Color.RED
        "#00FF00" -> Color.GREEN
        "#0000FF" -> Color.BLUE
        "#FFFF00" -> Color.YELLOW
        "#FF00FF" -> Color.MAGENTA
        "#00FFFF" -> Color.CYAN
        "#FFFFFF" -> Color.WHITE
        "#000000" -> Color.BLACK
        "#808080" -> Color.BRIGHT_BLACK
        "#FF8000" -> Color.YELLOW // Orange approximation
        "#FF0080" -> Color.MAGENTA // Hot Pink approximation
        "#00FF80" -> Color.GREEN  // Bright Green approximation
        "#0080FF" -> Color.CYAN   // Electric Blue approximation
        "#4080FF" -> Color.BLUE   // Soft Blue approximation
        "#8080FF" -> Color.MAGENTA // Soft Purple approximation
        "#40FF80" -> Color.GREEN  // Soft Green approximation
        "#C0C0C0" -> Color.WHITE  // Silver approximation
        else -> Color.WHITE // Default fallback
    }
}