package xyz.malefic.games

import kotlinx.coroutines.delay
import xyz.malefic.arcade.ArcadeSystem
import xyz.malefic.arcade.GameCartridge
import xyz.malefic.arcade.ScoreEntry
import kotlin.random.Random

/**
 * Tetris game implementation
 */
class TetrisCartridge : GameCartridge {
    override val name = "TETRIS"
    override val description = "Classic falling blocks puzzle"
    override val version = "1.0"
    override val icon = "🧱"

    private val boardWidth = 10
    private val boardHeight = 20
    private var board = Array(boardHeight) { BooleanArray(boardWidth) }
    private var score = 0
    private var level = 1
    private var lines = 0
    private var gameOver = false

    // Tetris pieces (7 standard tetrominoes)
    private val pieces =
        listOf(
            // I-piece
            listOf(
                listOf(1, 1, 1, 1),
            ),
            // O-piece
            listOf(
                listOf(1, 1),
                listOf(1, 1),
            ),
            // T-piece
            listOf(
                listOf(0, 1, 0),
                listOf(1, 1, 1),
            ),
            // S-piece
            listOf(
                listOf(0, 1, 1),
                listOf(1, 1, 0),
            ),
            // Z-piece
            listOf(
                listOf(1, 1, 0),
                listOf(0, 1, 1),
            ),
            // J-piece
            listOf(
                listOf(1, 0, 0),
                listOf(1, 1, 1),
            ),
            // L-piece
            listOf(
                listOf(0, 0, 1),
                listOf(1, 1, 1),
            ),
        )

    private var currentPiece = generateRandomPiece()
    private var currentX = boardWidth / 2 - 2
    private var currentY = 0

    override suspend fun play(arcade: ArcadeSystem) {
        // Reset game state
        board = Array(boardHeight) { BooleanArray(boardWidth) }
        score = 0
        level = 1
        lines = 0
        gameOver = false
        currentPiece = generateRandomPiece()
        currentX = boardWidth / 2 - 2
        currentY = 0

        var lastDrop = System.currentTimeMillis()
        val dropInterval = 1000 - (level - 1) * 100 // Speed increases with level

        while (!gameOver) {
            arcade.clearScreen()
            drawGame(arcade)

            // Handle input
            when (arcade.getInput(50)) {
                97, 65 -> movePiece(-1, 0) // 'a' or 'A' - left
                100, 68 -> movePiece(1, 0) // 'd' or 'D' - right
                115, 83 -> movePiece(0, 1) // 's' or 'S' - down
                119, 87 -> rotatePiece() // 'w' or 'W' - rotate
                113, 81 -> gameOver = true // 'q' or 'Q' - quit
            }

            // Auto-drop piece
            if (System.currentTimeMillis() - lastDrop > dropInterval) {
                if (!movePiece(0, 1)) {
                    // Piece can't move down, lock it
                    lockPiece()
                    checkLines()
                    currentPiece = generateRandomPiece()
                    currentX = boardWidth / 2 - 2
                    currentY = 0

                    // Check game over
                    if (isPieceColliding(currentPiece, currentX, currentY)) {
                        gameOver = true
                    }
                }
                lastDrop = System.currentTimeMillis()
            }

            delay(50)
        }

        // Game over screen
        showGameOver(arcade)
    }

    private fun generateRandomPiece(): List<List<Int>> = pieces[Random.nextInt(pieces.size)]

    private fun movePiece(
        dx: Int,
        dy: Int,
    ): Boolean {
        val newX = currentX + dx
        val newY = currentY + dy

        return if (!isPieceColliding(currentPiece, newX, newY)) {
            currentX = newX
            currentY = newY
            true
        } else {
            false
        }
    }

    private fun rotatePiece() {
        val rotated = rotatePieceClockwise(currentPiece)
        if (!isPieceColliding(rotated, currentX, currentY)) {
            currentPiece = rotated
        }
    }

    private fun rotatePieceClockwise(piece: List<List<Int>>): List<List<Int>> {
        val rows = piece.size
        val cols = piece[0].size
        return List(cols) { col ->
            List(rows) { row ->
                piece[rows - 1 - row][col]
            }
        }
    }

    private fun isPieceColliding(
        piece: List<List<Int>>,
        x: Int,
        y: Int,
    ): Boolean {
        piece.forEachIndexed { row, line ->
            line.forEachIndexed { col, cell ->
                if (cell == 1) {
                    val boardX = x + col
                    val boardY = y + row

                    if (boardX !in 0..<boardWidth ||
                        boardY >= boardHeight ||
                        (boardY >= 0 && board[boardY][boardX])
                    ) {
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun lockPiece() {
        currentPiece.forEachIndexed { row, line ->
            line.forEachIndexed { col, cell ->
                if (cell == 1) {
                    val boardX = currentX + col
                    val boardY = currentY + row
                    if (boardY >= 0 && boardX >= 0 && boardX < boardWidth) {
                        board[boardY][boardX] = true
                    }
                }
            }
        }
    }

    private fun checkLines() {
        var linesCleared = 0

        for (y in boardHeight - 1 downTo 0) {
            if (board[y].all { it }) {
                // Line is full, remove it
                for (moveY in y downTo 1) {
                    board[moveY] = board[moveY - 1].copyOf()
                }
                board[0] = BooleanArray(boardWidth)
                linesCleared++
            }
        }

        if (linesCleared > 0) {
            lines += linesCleared
            score += linesCleared * 100 * level
            level = (lines / 10) + 1
        }
    }

    private fun drawGame(arcade: ArcadeSystem) {
        // Draw title
        arcade.printAt(1, 1, "TETRIS", "\u001B[36m")

        // Draw board border
        for (y in 0..boardHeight + 1) {
            arcade.printAt(10, 3 + y, "║", "\u001B[37m")
            arcade.printAt(10 + boardWidth * 2 + 1, 3 + y, "║", "\u001B[37m")
        }

        for (x in 0..boardWidth * 2 + 1) {
            arcade.printAt(10 + x, 3, "═", "\u001B[37m")
            arcade.printAt(10 + x, 3 + boardHeight + 1, "═", "\u001B[37m")
        }

        // Draw board
        for (y in 0 until boardHeight) {
            for (x in 0 until boardWidth) {
                val char = if (board[y][x]) "██" else "  "
                val color = if (board[y][x]) "\u001B[35m" else ""
                arcade.printAt(11 + x * 2, 4 + y, char, color)
            }
        }

        // Draw current piece
        currentPiece.forEachIndexed { row, line ->
            line.forEachIndexed { col, cell ->
                if (cell == 1) {
                    val boardX = currentX + col
                    val boardY = currentY + row
                    if (boardY >= 0 && boardX >= 0 && boardX < boardWidth) {
                        arcade.printAt(11 + boardX * 2, 4 + boardY, "██", "\u001B[33m")
                    }
                }
            }
        }

        // Draw stats
        arcade.printAt(35, 5, "Score: $score", "\u001B[32m")
        arcade.printAt(35, 6, "Level: $level", "\u001B[32m")
        arcade.printAt(35, 7, "Lines: $lines", "\u001B[32m")

        // Draw controls
        arcade.printAt(35, 10, "Controls:", "\u001B[33m")
        arcade.printAt(35, 11, "A/D - Move", "\u001B[37m")
        arcade.printAt(35, 12, "S - Drop", "\u001B[37m")
        arcade.printAt(35, 13, "W - Rotate", "\u001B[37m")
        arcade.printAt(35, 14, "Q - Quit", "\u001B[37m")
    }

    private fun showGameOver(arcade: ArcadeSystem) {
        arcade.clearScreen()
        arcade.printAt(1, 10, "GAME OVER", "\u001B[31m")
        arcade.printAt(1, 12, "Final Score: $score", "\u001B[32m")
        arcade.printAt(1, 13, "Level: $level", "\u001B[32m")
        arcade.printAt(1, 14, "Lines: $lines", "\u001B[32m")

        arcade.printAt(1, 16, "Enter your name (max 10 chars): ", "\u001B[33m")

        var name = ""
        while (name.length < 10) {
            val input = arcade.getInput(5000)
            if (input != null) {
                when (input) {
                    13, 10 -> break // Enter
                    8, 127 -> if (name.isNotEmpty()) name = name.dropLast(1) // Backspace
                    in 32..126 -> name += input.toChar() // Printable characters
                }
                arcade.printAt(34, 16, name + "_", "\u001B[37m")
            }
        }

        if (name.isNotEmpty()) {
            val scoreEntry = ScoreEntry(name, score, arcade.getCurrentDateTime(), level)
            val isHighScore = arcade.getScoreManager().addScore("tetris", scoreEntry)

            if (isHighScore) {
                arcade.printAt(1, 18, "NEW HIGH SCORE!", "\u001B[32m")
            }
        }

        arcade.printAt(1, 20, "Press any key to continue...", "\u001B[90m")
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
