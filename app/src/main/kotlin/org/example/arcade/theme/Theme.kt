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
 */
private fun parseHexColor(hex: String): Color? {
    return try {
        val cleanHex = hex.removePrefix("#")
        if (cleanHex.length == 6) {
            val r = cleanHex.substring(0, 2).toInt(16)
            val g = cleanHex.substring(2, 4).toInt(16)
            val b = cleanHex.substring(4, 6).toInt(16)
            Color.RGB(r, g, b)
        } else {
            null
        }
    } catch (e: Exception) {
        null
    }
}