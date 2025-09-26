package org.example.arcade.theme

import com.charleskorn.kaml.Yaml
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.IOException

/**
 * Manages themes for the arcade system
 */
class ThemeManager(
    private val dataDir: String = "arcade-data/themes",
) {
    private val json = Json { prettyPrint = true }
    private val yaml = Yaml.default
    private val httpClient = OkHttpClient()

    // Current active theme
    private var _currentTheme: Theme = getDefaultTheme("classic")
    val currentTheme: Theme get() = _currentTheme

    init {
        File(dataDir).mkdirs()
        loadCurrentTheme()
    }

    /**
     * Get all available default themes
     */
    fun getDefaultThemes(): List<Theme> =
        listOf(
            getDefaultTheme("classic"),
            getDefaultTheme("neon"),
            getDefaultTheme("minimal"),
            getDefaultTheme("dark"),
            getDefaultTheme("retro"),
        )

    /**
     * Get a default theme by name
     */
    private fun getDefaultTheme(name: String): Theme =
        when (name) {
            "neon" ->
                Theme(
                    name = "Neon",
                    description = "Bright neon colors for a cyberpunk feel",
                    author = "ArcadeTUI",
                    colors =
                        ThemeColors(
                            primary = "#FF0080", // Hot Pink
                            secondary = "#00FF80", // Bright Green
                            accent = "#0080FF", // Electric Blue
                            background = "#000000", // Black
                            text = "#FFFFFF", // White
                            textDim = "#808080", // Gray
                            success = "#00FF80", // Bright Green
                            warning = "#FFFF00", // Yellow
                            error = "#FF4040", // Bright Red
                            gameArea = "#FF0080", // Hot Pink
                            player = "#00FF80", // Bright Green
                            enemy = "#FF4040", // Bright Red
                            bullet = "#FFFFFF", // White
                            border = "#0080FF", // Electric Blue
                        ),
                )
            "minimal" ->
                Theme(
                    name = "Minimal",
                    description = "Clean monochrome theme",
                    author = "ArcadeTUI",
                    colors =
                        ThemeColors(
                            primary = "#FFFFFF", // White
                            secondary = "#C0C0C0", // Silver
                            accent = "#808080", // Gray
                            background = "#000000", // Black
                            text = "#FFFFFF", // White
                            textDim = "#808080", // Gray
                            success = "#FFFFFF", // White
                            warning = "#C0C0C0", // Silver
                            error = "#808080", // Gray
                            gameArea = "#FFFFFF", // White
                            player = "#FFFFFF", // White
                            enemy = "#808080", // Gray
                            bullet = "#C0C0C0", // Silver
                            border = "#808080", // Gray
                        ),
                )
            "dark" ->
                Theme(
                    name = "Dark",
                    description = "Dark theme with muted colors",
                    author = "ArcadeTUI",
                    colors =
                        ThemeColors(
                            primary = "#4080FF", // Soft Blue
                            secondary = "#8080FF", // Soft Purple
                            accent = "#40FF80", // Soft Green
                            background = "#000000", // Black
                            text = "#C0C0C0", // Light Gray
                            textDim = "#606060", // Dark Gray
                            success = "#40FF80", // Soft Green
                            warning = "#FFFF40", // Soft Yellow
                            error = "#FF4040", // Soft Red
                            gameArea = "#C0C0C0", // Light Gray
                            player = "#4080FF", // Soft Blue
                            enemy = "#FF4040", // Soft Red
                            bullet = "#C0C0C0", // Light Gray
                            border = "#606060", // Dark Gray
                        ),
                )
            "retro" ->
                Theme(
                    name = "Retro",
                    description = "Classic 80s arcade colors",
                    author = "ArcadeTUI",
                    colors =
                        ThemeColors(
                            primary = "#FF8000", // Orange
                            secondary = "#FFFF00", // Yellow
                            accent = "#FF0080", // Magenta
                            background = "#000000", // Black
                            text = "#FFFFFF", // White
                            textDim = "#808080", // Gray
                            success = "#00FF00", // Green
                            warning = "#FFFF00", // Yellow
                            error = "#FF0000", // Red
                            gameArea = "#FFFFFF", // White
                            player = "#FF8000", // Orange
                            enemy = "#FF0080", // Magenta
                            bullet = "#FFFF00", // Yellow
                            border = "#00FF00", // Green
                        ),
                )
            else ->
                Theme( // "classic"
                    name = "Classic",
                    description = "Original ArcadeTUI theme",
                    author = "ArcadeTUI",
                    colors = ThemeColors(), // Default colors
                )
        }

    /**
     * Get all custom themes from local storage
     */
    fun getCustomThemes(): List<Theme> {
        val customDir = File(dataDir, "custom")
        if (!customDir.exists()) return emptyList()

        return customDir
            .listFiles { file -> file.extension == "json" }
            ?.mapNotNull { file ->
                try {
                    json.decodeFromString<Theme>(file.readText())
                } catch (_: Exception) {
                    null
                }
            } ?: emptyList()
    }

    /**
     * Get all available themes (default + custom)
     */
    fun getAllThemes(): List<Theme> = getDefaultThemes() + getCustomThemes()

    /**
     * Set the current active theme
     */
    fun setCurrentTheme(theme: Theme) {
        _currentTheme = theme
        saveCurrentTheme()
    }

    /**
     * Download and add a theme from URL
     */
    suspend fun addThemeFromUrl(
        url: String,
        dispatcher: CoroutineDispatcher = Dispatchers.IO,
    ): Result<Theme> =
        withContext(dispatcher) {
            try {
                val request =
                    Request
                        .Builder()
                        .url(url)
                        .build()

                val response = httpClient.newCall(request).execute()
                if (!response.isSuccessful) {
                    return@withContext Result.failure(IOException("Failed to download theme: ${response.code}"))
                }

                val content = response.body?.string() ?: ""
                val theme =
                    if (url.endsWith(".yaml") || url.endsWith(".yml")) {
                        yaml.decodeFromString<Theme>(content)
                    } else {
                        json.decodeFromString<Theme>(content)
                    }

                // Save as custom theme
                saveCustomTheme(theme)
                Result.success(theme)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }

    /**
     * Update a custom theme from its original URL
     */
    suspend fun updateTheme(
        theme: Theme,
        url: String,
    ): Result<Theme> = addThemeFromUrl(url)

    /**
     * Delete a custom theme
     */
    fun deleteCustomTheme(theme: Theme): Boolean {
        val customDir = File(dataDir, "custom")
        val themeFile = File(customDir, "${theme.name.lowercase().replace(" ", "_")}.json")
        return if (themeFile.exists()) {
            themeFile.delete()
        } else {
            false
        }
    }

    /**
     * Save a custom theme to local storage
     */
    private fun saveCustomTheme(theme: Theme) {
        val customDir = File(dataDir, "custom")
        customDir.mkdirs()

        val themeFile = File(customDir, "${theme.name.lowercase().replace(" ", "_")}.json")
        themeFile.writeText(json.encodeToString(theme))
    }

    /**
     * Save current theme preference
     */
    private fun saveCurrentTheme() {
        val configFile = File(dataDir, "current_theme.json")
        val config = mapOf("currentTheme" to _currentTheme.name)
        configFile.writeText(json.encodeToString(config))
    }

    /**
     * Load current theme preference
     */
    private fun loadCurrentTheme() {
        val configFile = File(dataDir, "current_theme.json")
        if (configFile.exists()) {
            try {
                val config = json.decodeFromString<Map<String, String>>(configFile.readText())
                val themeName = config["currentTheme"] ?: "Classic"
                _currentTheme = getAllThemes().find { it.name == themeName } ?: getDefaultTheme("classic")
            } catch (_: Exception) {
                _currentTheme = getDefaultTheme("classic")
            }
        }
    }
}
