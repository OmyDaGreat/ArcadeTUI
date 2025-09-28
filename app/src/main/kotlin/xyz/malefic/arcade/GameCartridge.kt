package xyz.malefic.arcade

/**
 * Represents a game cartridge in the arcade system.
 * Each game implements this interface to provide a modular system.
 */
interface GameCartridge {
    val name: String
    val description: String
    val version: String
    val icon: String // ASCII art icon

    /**
     * Initialize the game and start playing
     */
    suspend fun play(arcade: ArcadeSystem)

    /**
     * Get the high scores for this game from the arcade system
     */
    fun getHighScores(scoreManager: ScoreManager): List<ScoreEntry> = scoreManager.loadScores(name.lowercase())

    /**
     * Reset high scores for this game
     */
    fun resetHighScores(scoreManager: ScoreManager) {
        scoreManager.resetScores(name.lowercase())
    }

    // Default implementations that delegate to arcade system
    fun getHighScores(): List<ScoreEntry> = emptyList()

    fun resetHighScores() = Unit
}

/**
 * Represents a score entry in the high score table
 */
data class ScoreEntry(
    val playerName: String,
    val score: Int,
    val date: String,
    val level: Int = 1,
)
