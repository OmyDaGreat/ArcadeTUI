package xyz.malefic.arcade.theme

import com.googlecode.lanterna.TextColor
import kotlinx.serialization.Serializable

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
    val styles: ThemeStyles = ThemeStyles(),
) {
    fun toLanternaColors(): LanternaThemeColors =
        LanternaThemeColors(
            primary = parseHexColor(colors.primary) ?: TextColor.ANSI.CYAN,
            secondary = parseHexColor(colors.secondary) ?: TextColor.ANSI.YELLOW,
            accent = parseHexColor(colors.accent) ?: TextColor.ANSI.GREEN,
            background = parseHexColor(colors.background) ?: TextColor.ANSI.BLACK,
            text = parseHexColor(colors.text) ?: TextColor.ANSI.WHITE,
            textDim = parseHexColor(colors.textDim) ?: TextColor.ANSI.BLACK_BRIGHT,
            success = parseHexColor(colors.success) ?: TextColor.ANSI.GREEN,
            warning = parseHexColor(colors.warning) ?: TextColor.ANSI.YELLOW,
            error = parseHexColor(colors.error) ?: TextColor.ANSI.RED,
            gameArea = parseHexColor(colors.gameArea) ?: TextColor.ANSI.WHITE,
            player = parseHexColor(colors.player) ?: TextColor.ANSI.YELLOW,
            enemy = parseHexColor(colors.enemy) ?: TextColor.ANSI.RED,
            bullet = parseHexColor(colors.bullet) ?: TextColor.ANSI.WHITE,
            border = parseHexColor(colors.border) ?: TextColor.ANSI.WHITE,
        )
}

@Serializable
data class ThemeColors(
    val primary: String = "#00FFFF", // Cyan
    val secondary: String = "#FFFF00", // Yellow
    val accent: String = "#00FF00", // Green
    val background: String = "#000000", // Black
    val text: String = "#FFFFFF", // White
    val textDim: String = "#808080", // Gray
    val success: String = "#00FF00", // Green
    val warning: String = "#FFFF00", // Yellow
    val error: String = "#FF0000", // Red
    val gameArea: String = "#FFFFFF", // White
    val player: String = "#FFFF00", // Yellow
    val enemy: String = "#FF0000", // Red
    val bullet: String = "#FFFFFF", // White
    val border: String = "#FFFFFF", // White
)

@Serializable
data class ThemeStyles(
    val logoStyle: String = "bold",
    val menuStyle: String = "normal",
    val selectedStyle: String = "bold",
    val borderStyle: String = "normal",
)

/**
 * Lanterna-compatible theme colors
 */
data class LanternaThemeColors(
    val primary: TextColor,
    val secondary: TextColor,
    val accent: TextColor,
    val background: TextColor,
    val text: TextColor,
    val textDim: TextColor,
    val success: TextColor,
    val warning: TextColor,
    val error: TextColor,
    val gameArea: TextColor,
    val player: TextColor,
    val enemy: TextColor,
    val bullet: TextColor,
    val border: TextColor,
)

/**
 * Extension function to parse hex color strings to Lanterna TextColor
 * Uses predefined colors for now, with RGB support for custom colors
 */
private fun parseHexColor(hex: String): TextColor? =
    when (hex.uppercase()) {
        "#FF0000" -> TextColor.ANSI.RED
        "#00FF00" -> TextColor.ANSI.GREEN
        "#0000FF" -> TextColor.ANSI.BLUE
        "#FFFF00" -> TextColor.ANSI.YELLOW
        "#FF00FF" -> TextColor.ANSI.MAGENTA
        "#00FFFF" -> TextColor.ANSI.CYAN
        "#FFFFFF" -> TextColor.ANSI.WHITE
        "#000000" -> TextColor.ANSI.BLACK
        "#808080" -> TextColor.ANSI.BLACK_BRIGHT
        "#FF8000" -> TextColor.ANSI.YELLOW // Orange approximation
        "#FF0080" -> TextColor.ANSI.MAGENTA // Hot Pink approximation
        "#00FF80" -> TextColor.ANSI.GREEN // Bright Green approximation
        "#0080FF" -> TextColor.ANSI.CYAN // Electric Blue approximation
        "#4080FF" -> TextColor.ANSI.BLUE // Soft Blue approximation
        "#8080FF" -> TextColor.ANSI.MAGENTA // Soft Purple approximation
        "#40FF80" -> TextColor.ANSI.GREEN // Soft Green approximation
        "#C0C0C0" -> TextColor.ANSI.WHITE // Silver approximation
        else -> {
            // Try to parse as RGB hex
            if (hex.startsWith("#") && hex.length == 7) {
                try {
                    val r = hex.substring(1, 3).toInt(16)
                    val g = hex.substring(3, 5).toInt(16)
                    val b = hex.substring(5, 7).toInt(16)
                    TextColor.RGB(r, g, b)
                } catch (e: NumberFormatException) {
                    TextColor.ANSI.WHITE // Default fallback
                }
            } else {
                TextColor.ANSI.WHITE // Default fallback
            }
        }
    }
