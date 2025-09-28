package xyz.malefic.arcade

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Manages persistent high score storage for games
 */
class ScoreManager(
    private val dataDir: String = "arcade-data",
) {
    private val json = Json { prettyPrint = true }

    init {
        File(dataDir).mkdirs()
    }

    /**
     * Load high scores for a specific game
     */
    fun loadScores(gameName: String): List<ScoreEntry> {
        val file = File(dataDir, "${gameName.lowercase()}-scores.json")
        return if (file.exists()) {
            try {
                val scoresData = json.decodeFromString<ScoresData>(file.readText())
                scoresData.scores.map {
                    ScoreEntry(it.playerName, it.score, it.date, it.level)
                }
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }
    }

    /**
     * Save high scores for a specific game
     */
    fun saveScores(
        gameName: String,
        scores: List<ScoreEntry>,
    ) {
        val file = File(dataDir, "${gameName.lowercase()}-scores.json")
        val scoresData =
            ScoresData(
                gameName = gameName,
                scores =
                    scores.map {
                        SerializableScoreEntry(it.playerName, it.score, it.date, it.level)
                    },
            )
        file.writeText(json.encodeToString(scoresData))
    }

    /**
     * Add a new score and maintain top 10 list
     */
    fun addScore(
        gameName: String,
        scoreEntry: ScoreEntry,
    ): Boolean {
        val currentScores = loadScores(gameName).toMutableList()
        currentScores.add(scoreEntry)
        currentScores.sortByDescending { it.score }

        val wasHighScore = currentScores.indexOf(scoreEntry) < 10

        // Keep only top 10
        val topScores = currentScores.take(10)
        saveScores(gameName, topScores)

        return wasHighScore
    }

    /**
     * Reset scores for a game
     */
    fun resetScores(gameName: String) {
        val file = File(dataDir, "${gameName.lowercase()}-scores.json")
        if (file.exists()) {
            file.delete()
        }
    }
}

@Serializable
private data class ScoresData(
    val gameName: String,
    val scores: List<SerializableScoreEntry>,
)

@Serializable
private data class SerializableScoreEntry(
    val playerName: String,
    val score: Int,
    val date: String,
    val level: Int = 1,
)
