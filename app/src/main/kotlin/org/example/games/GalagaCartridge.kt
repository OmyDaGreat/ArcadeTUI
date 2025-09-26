package org.example.games

import kotlinx.coroutines.delay
import org.example.arcade.ArcadeSystem
import org.example.arcade.GameCartridge
import org.example.arcade.ScoreEntry
import kotlin.random.Random

/**
 * Galaga-style space shooter game
 */
class GalagaCartridge : GameCartridge {
    override val name = "GALAGA"
    override val description = "Classic space shooter"
    override val version = "1.0"
    override val icon = "🚀"

    private val screenWidth = 60
    private val screenHeight = 20

    private var playerX = screenWidth / 2
    private var playerY = screenHeight - 2
    private var score = 0
    private var lives = 3
    private var level = 1
    private var gameOver = false

    private data class Bullet(
        var x: Int,
        var y: Int,
        val isPlayerBullet: Boolean,
    )

    private data class Enemy(
        var x: Int,
        var y: Int,
        var type: Char = '*',
    )

    private val bullets = mutableListOf<Bullet>()
    private val enemies = mutableListOf<Enemy>()

    override suspend fun play(arcade: ArcadeSystem) {
        // Reset game state
        playerX = screenWidth / 2
        playerY = screenHeight - 2
        score = 0
        lives = 3
        level = 1
        gameOver = false
        bullets.clear()
        enemies.clear()

        var lastEnemySpawn = System.currentTimeMillis()
        var lastUpdate = System.currentTimeMillis()
        var shotCooldown = 0L

        // Initial enemy wave
        spawnEnemyWave()

        while (!gameOver && lives > 0) {
            val currentTime = System.currentTimeMillis()

            arcade.clearScreen()
            drawGame(arcade)

            // Handle input
            when (arcade.getInput(50)) {
                97, 65 -> if (playerX > 1) playerX-- // 'a' or 'A' - left
                100, 68 -> if (playerX < screenWidth - 2) playerX++ // 'd' or 'D' - right
                32 -> { // Space - shoot
                    if (currentTime - shotCooldown > 200) { // 200ms cooldown
                        bullets.add(Bullet(playerX, playerY - 1, true))
                        shotCooldown = currentTime
                    }
                }
                113, 81 -> gameOver = true // 'q' or 'Q' - quit
            }

            // Update game state
            if (currentTime - lastUpdate > 100) {
                updateBullets()
                updateEnemies()
                checkCollisions()
                lastUpdate = currentTime
            }

            // Spawn new enemies
            if (currentTime - lastEnemySpawn > 2000) {
                if (enemies.isEmpty() || Random.nextFloat() < 0.3f) {
                    spawnRandomEnemies()
                }
                lastEnemySpawn = currentTime
            }

            // Level progression
            if (enemies.isEmpty()) {
                level++
                spawnEnemyWave()
            }

            delay(50)
        }

        showGameOver(arcade)
    }

    private fun spawnEnemyWave() {
        val waveSize = 5 + level * 2
        (0 until waveSize).forEach { _ ->
            val x = Random.nextInt(2, screenWidth - 2)
            val y = Random.nextInt(1, 5)
            val type =
                when (Random.nextInt(3)) {
                    0 -> '*' // Basic enemy
                    1 -> '#' // Stronger enemy
                    else -> '@' // Boss enemy
                }
            enemies.add(Enemy(x, y, type))
        }
    }

    private fun spawnRandomEnemies() {
        repeat(Random.nextInt(1, 4)) {
            val x = Random.nextInt(2, screenWidth - 2)
            enemies.add(Enemy(x, 1, if (Random.nextFloat() < 0.7f) '*' else '#'))
        }
    }

    private fun updateBullets() {
        bullets.removeAll { bullet ->
            if (bullet.isPlayerBullet) {
                bullet.y--
                bullet.y < 0
            } else {
                bullet.y++
                bullet.y >= screenHeight
            }
        }

        // Enemy shooting
        if (Random.nextFloat() < 0.05f) {
            val shootingEnemies = enemies.filter { it.y < screenHeight - 5 }
            if (shootingEnemies.isNotEmpty()) {
                val shooter = shootingEnemies.random()
                bullets.add(Bullet(shooter.x, shooter.y + 1, false))
            }
        }
    }

    private fun updateEnemies() {
        enemies.forEach { enemy ->
            if (Random.nextFloat() < 0.02f) {
                enemy.y++
            }
            if (Random.nextFloat() < 0.05f) {
                enemy.x += if (Random.nextBoolean()) 1 else -1
                enemy.x = enemy.x.coerceIn(1, screenWidth - 2)
            }
        }

        // Remove enemies that reached the bottom
        enemies.removeAll { it.y >= screenHeight - 2 }
    }

    private fun checkCollisions() {
        // Player bullets hitting enemies
        val playerBullets = bullets.filter { it.isPlayerBullet }
        playerBullets.forEach { bullet ->
            val hitEnemy =
                enemies.find { enemy ->
                    bullet.x == enemy.x && bullet.y == enemy.y
                }
            if (hitEnemy != null) {
                bullets.remove(bullet)
                enemies.remove(hitEnemy)

                score +=
                    when (hitEnemy.type) {
                        '*' -> 100
                        '#' -> 200
                        '@' -> 500
                        else -> 100
                    }
            }
        }

        // Enemy bullets hitting player
        val enemyBullets = bullets.filter { !it.isPlayerBullet }
        enemyBullets.forEach { bullet ->
            if (bullet.x == playerX && bullet.y == playerY) {
                bullets.remove(bullet)
                lives--
                if (lives <= 0) {
                    gameOver = true
                }
            }
        }

        // Enemies reaching player
        enemies.forEach { enemy ->
            if (enemy.x == playerX && enemy.y == playerY) {
                lives--
                enemies.remove(enemy)
                if (lives <= 0) {
                    gameOver = true
                }
            }
        }
    }

    private fun drawGame(arcade: ArcadeSystem) {
        // Draw title and stats
        arcade.printAt(1, 1, "GALAGA", "\u001B[36m")
        arcade.printAt(screenWidth - 20, 1, "Score: $score", "\u001B[32m")
        arcade.printAt(screenWidth - 20, 2, "Lives: $lives", "\u001B[31m")
        arcade.printAt(screenWidth - 20, 3, "Level: $level", "\u001B[33m")

        // Draw game area border
        for (y in 4..screenHeight + 3) {
            arcade.printAt(1, y, "│", "\u001B[37m")
            arcade.printAt(screenWidth + 2, y, "│", "\u001B[37m")
        }
        for (x in 1..screenWidth + 2) {
            arcade.printAt(x, 4, "─", "\u001B[37m")
            arcade.printAt(x, screenHeight + 4, "─", "\u001B[37m")
        }

        // Draw player
        arcade.printAt(2 + playerX, 4 + playerY, "▲", "\u001B[32m")

        // Draw enemies
        enemies.forEach { enemy ->
            val color =
                when (enemy.type) {
                    '*' -> "\u001B[31m" // Red for basic enemies
                    '#' -> "\u001B[35m" // Magenta for stronger enemies
                    '@' -> "\u001B[91m" // Bright red for boss enemies
                    else -> "\u001B[31m"
                }
            arcade.printAt(2 + enemy.x, 4 + enemy.y, enemy.type.toString(), color)
        }

        // Draw bullets
        bullets.forEach { bullet ->
            val char = if (bullet.isPlayerBullet) "│" else "!"
            val color = if (bullet.isPlayerBullet) "\u001B[33m" else "\u001B[31m"
            arcade.printAt(2 + bullet.x, 4 + bullet.y, char, color)
        }

        // Draw controls
        arcade.printAt(1, screenHeight + 6, "Controls:", "\u001B[33m")
        arcade.printAt(1, screenHeight + 7, "A/D - Move   Space - Shoot   Q - Quit", "\u001B[37m")
    }

    private fun showGameOver(arcade: ArcadeSystem) {
        arcade.clearScreen()
        arcade.printAt(1, 10, "GAME OVER", "\u001B[31m")
        arcade.printAt(1, 12, "Final Score: $score", "\u001B[32m")
        arcade.printAt(1, 13, "Level Reached: $level", "\u001B[32m")

        arcade.printAt(1, 15, "Enter your name (max 10 chars): ", "\u001B[33m")

        var name = ""
        while (name.length < 10) {
            val input = arcade.getInput(5000)
            if (input != null) {
                when (input) {
                    13, 10 -> break // Enter
                    8, 127 -> if (name.isNotEmpty()) name = name.dropLast(1) // Backspace
                    in 32..126 -> name += input.toChar() // Printable characters
                }
                arcade.printAt(34, 15, name + "_", "\u001B[37m")
            }
        }

        if (name.isNotEmpty()) {
            val scoreEntry = ScoreEntry(name, score, arcade.getCurrentDateTime(), level)
            val isHighScore = arcade.getScoreManager().addScore("galaga", scoreEntry)

            if (isHighScore) {
                arcade.printAt(1, 17, "NEW HIGH SCORE!", "\u001B[32m")
            }
        }

        arcade.printAt(1, 19, "Press any key to continue...", "\u001B[90m")
        arcade.getInput(10000)
    }

    override fun getHighScores(): List<ScoreEntry> {
        // This will be handled by the arcade system's score manager
        return emptyList()
    }

    override fun resetHighScores() {
        // This will be handled by the arcade system's score manager
    }
}
