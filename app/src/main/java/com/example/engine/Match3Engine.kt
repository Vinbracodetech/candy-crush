package com.example.engine

import com.example.data.model.CandyColor
import com.example.data.model.CandyTile
import com.example.data.model.GoalType
import com.example.data.model.LevelConfig
import com.example.data.model.LevelGoal
import com.example.data.model.Particle
import com.example.data.model.ScorePopup
import com.example.data.model.SpecialType
import com.example.data.model.TileObstacle
import kotlinx.coroutines.delay
import java.util.UUID
import kotlin.random.Random

data class MatchEngineState(
    val grid: List<List<CandyTile?>>,
    val obstacles: Map<Pair<Int, Int>, TileObstacle>,
    val movesLeft: Int,
    val currentScore: Int,
    val goals: List<LevelGoal>,
    val comboMultiplier: Int = 1,
    val isGameOver: Boolean = false,
    val isGameWon: Boolean = false,
    val isBusy: Boolean = false,
    val activeBooster: String? = null,
    val selectedTile: Pair<Int, Int>? = null,
    val lastComboMessage: String? = null,
    val scorePopups: List<ScorePopup> = emptyList(),
    val particles: List<Particle> = emptyList()
)

class Match3Engine(
    val config: LevelConfig,
    private val onSound: (String, Int) -> Unit = { _, _ -> },
    private val onHaptic: () -> Unit = {}
) {
    private val rows = config.rows
    private val cols = config.cols
    private val board: Array<Array<CandyTile?>> = Array(rows) { arrayOfNulls(cols) }
    private val obstacles: MutableMap<Pair<Int, Int>, TileObstacle> = config.initialObstacles.toMutableMap()
    private val emptyCells = config.emptyTiles

    var movesLeft = config.maxMoves
    var score = 0
    val goals = config.goals.map { it.copy() }.toMutableList()
    var isGameOver = false
    var isGameWon = false
    var isBusy = false
    var selectedTile: Pair<Int, Int>? = null
    var activeBooster: String? = null
    var lastComboMessage: String? = null
    val scorePopups = mutableListOf<ScorePopup>()
    val particles = mutableListOf<Particle>()

    init {
        initializeBoard()
    }

    fun getState(): MatchEngineState {
        val gridList = board.map { row -> row.toList() }
        return MatchEngineState(
            grid = gridList,
            obstacles = obstacles.toMap(),
            movesLeft = movesLeft,
            currentScore = score,
            goals = goals.map { it.copy() },
            isGameOver = isGameOver,
            isGameWon = isGameWon,
            isBusy = isBusy,
            activeBooster = activeBooster,
            selectedTile = selectedTile,
            lastComboMessage = lastComboMessage,
            scorePopups = scorePopups.toList(),
            particles = particles.toList()
        )
    }

    private fun initializeBoard() {
        var attempts = 0
        do {
            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    if (r to c in emptyCells) {
                        board[r][c] = null
                    } else {
                        val color = CandyColor.random(config.colorCount)
                        board[r][c] = CandyTile(
                            id = UUID.randomUUID().toString(),
                            row = r,
                            col = c,
                            color = color
                        )
                    }
                }
            }
            // Remove initial matches
            clearInitialMatches()
            attempts++
        } while (!hasValidMoves() && attempts < 20)

        // Spawn initial ingredient if configured
        if (config.ingredientSpawnCols.isNotEmpty() && config.maxIngredientsOnBoard > 0) {
            val col = config.ingredientSpawnCols.random()
            if (0 to col !in emptyCells) {
                board[0][col] = CandyTile(
                    id = UUID.randomUUID().toString(),
                    row = 0,
                    col = col,
                    color = CandyColor.RED,
                    isIngredient = true
                )
            }
        }
    }

    private fun clearInitialMatches() {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (board[r][c] == null) continue
                while (
                    (c >= 2 && board[r][c]?.color == board[r][c - 1]?.color && board[r][c]?.color == board[r][c - 2]?.color) ||
                    (r >= 2 && board[r][c]?.color == board[r - 1][c]?.color && board[r][c]?.color == board[r - 2][c]?.color)
                ) {
                    board[r][c] = CandyTile(
                        id = UUID.randomUUID().toString(),
                        row = r,
                        col = c,
                        color = CandyColor.random(config.colorCount)
                    )
                }
            }
        }
    }

    suspend fun onTileClicked(r: Int, c: Int, onStateUpdate: (MatchEngineState) -> Unit) {
        if (isBusy || isGameOver || isGameWon) return
        if (r < 0 || c < 0 || r >= config.rows || c >= config.cols) return
        if (r to c in emptyCells || board[r][c] == null) return

        // Handle Active Booster Click
        if (activeBooster != null) {
            handleBoosterUse(r, c, onStateUpdate)
            return
        }

        val currentSelected = selectedTile
        if (currentSelected == null) {
            selectedTile = r to c
            updateTileSelection(r, c, true)
            onSound("click", 1)
            onStateUpdate(getState())
        } else {
            val (sr, sc) = currentSelected
            if (sr == r && sc == c) {
                // Deselect
                selectedTile = null
                updateTileSelection(sr, sc, false)
                onStateUpdate(getState())
            } else if (isAdjacent(sr, sc, r, c)) {
                selectedTile = null
                updateTileSelection(sr, sc, false)
                onStateUpdate(getState())
                attemptSwap(sr, sc, r, c, onStateUpdate)
            } else {
                // Select different tile
                updateTileSelection(sr, sc, false)
                selectedTile = r to c
                updateTileSelection(r, c, true)
                onSound("click", 1)
                onStateUpdate(getState())
            }
        }
    }

    private fun updateTileSelection(r: Int, c: Int, isSelected: Boolean) {
        val tile = board[r][c] ?: return
        board[r][c] = tile.copy(isSelected = isSelected)
    }

    private fun isAdjacent(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        return (Math.abs(r1 - r2) == 1 && c1 == c2) || (Math.abs(c1 - c2) == 1 && r1 == r2)
    }

    private suspend fun attemptSwap(r1: Int, c1: Int, r2: Int, c2: Int, onStateUpdate: (MatchEngineState) -> Unit) {
        isBusy = true
        onSound("swap", 1)
        onHaptic()

        val tile1 = board[r1][c1] ?: return
        val tile2 = board[r2][c2] ?: return

        // Check for Special + Special combo first
        val isSpecialCombo = (tile1.special != SpecialType.NONE && tile2.special != SpecialType.NONE) ||
                (tile1.special == SpecialType.COLOR_BOMB || tile2.special == SpecialType.COLOR_BOMB)

        // Perform swap
        board[r1][c1] = tile2.copy(row = r1, col = c1)
        board[r2][c2] = tile1.copy(row = r2, col = c2)
        onStateUpdate(getState())
        delay(30)

        if (isSpecialCombo) {
            movesLeft--
            executeSpecialCombo(r1, c1, r2, c2, tile1, tile2, onStateUpdate)
            resolveCascades(1, onStateUpdate)
            checkLevelStatus()
            isBusy = false
            onStateUpdate(getState())
            return
        }

        val matches = findMatches()
        if (matches.isNotEmpty()) {
            movesLeft--
            resolveCascades(1, onStateUpdate)
            checkLevelStatus()
        } else {
            // Revert swap
            onSound("invalid", 1)
            board[r1][c1] = tile1
            board[r2][c2] = tile2
            onStateUpdate(getState())
            delay(50)
        }

        isBusy = false
        onStateUpdate(getState())
    }

    private suspend fun executeSpecialCombo(
        r1: Int, c1: Int, r2: Int, c2: Int,
        t1: CandyTile, t2: CandyTile,
        onStateUpdate: (MatchEngineState) -> Unit
    ) {
        val s1 = t1.special
        val s2 = t2.special

        when {
            // Color Bomb + Color Bomb -> Wipe Entire Board
            s1 == SpecialType.COLOR_BOMB && s2 == SpecialType.COLOR_BOMB -> {
                onSound("bomb", 5)
                onHaptic()
                showComboPopup("COSMIC NOVA!", (c1 + c2) / 2f, (r1 + r2) / 2f)
                addScore(5000, (c1 + c2) / 2f, (r1 + r2) / 2f)
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        board[r][c]?.let { clearTile(r, c) }
                    }
                }
                delay(30)
            }

            // Color Bomb + Striped -> Convert all same color candies to striped and explode
            (s1 == SpecialType.COLOR_BOMB && (s2 == SpecialType.STRIPED_HORIZONTAL || s2 == SpecialType.STRIPED_VERTICAL)) ||
                    (s2 == SpecialType.COLOR_BOMB && (s1 == SpecialType.STRIPED_HORIZONTAL || s1 == SpecialType.STRIPED_VERTICAL)) -> {
                val targetColor = if (s1 == SpecialType.COLOR_BOMB) t2.color else t1.color
                onSound("color_bomb", 4)
                showComboPopup("SUGAR STORM!", (c1 + c2) / 2f, (r1 + r2) / 2f)
                clearTile(r1, c1)
                clearTile(r2, c2)
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if (board[r][c]?.color == targetColor) {
                            board[r][c] = board[r][c]?.copy(
                                special = if (Random.nextBoolean()) SpecialType.STRIPED_HORIZONTAL else SpecialType.STRIPED_VERTICAL
                            )
                        }
                    }
                }
                onStateUpdate(getState())
                delay(50)
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if (board[r][c]?.color == targetColor) {
                            detonateSpecial(r, c, board[r][c]!!.special)
                        }
                    }
                }
            }

            // Color Bomb + Normal -> Clear all of that color
            s1 == SpecialType.COLOR_BOMB || s2 == SpecialType.COLOR_BOMB -> {
                val targetColor = if (s1 == SpecialType.COLOR_BOMB) t2.color else t1.color
                onSound("color_bomb", 3)
                showComboPopup("TASTY POP!", (c1 + c2) / 2f, (r1 + r2) / 2f)
                clearTile(r1, c1)
                clearTile(r2, c2)
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if (board[r][c]?.color == targetColor) {
                            clearTile(r, c)
                        }
                    }
                }
                delay(50)
            }

            // Striped + Striped -> Cross blast (Row and Column)
            (s1 == SpecialType.STRIPED_HORIZONTAL || s1 == SpecialType.STRIPED_VERTICAL) &&
                    (s2 == SpecialType.STRIPED_HORIZONTAL || s2 == SpecialType.STRIPED_VERTICAL) -> {
                onSound("line", 2)
                showComboPopup("SUPER CROSS!", c2.toFloat(), r2.toFloat())
                clearRow(r2)
                clearCol(c2)
                delay(50)
            }

            // Striped + Wrapped -> Giant 3-row + 3-col cross blast
            (s1 == SpecialType.WRAPPED_BOMB && (s2 == SpecialType.STRIPED_HORIZONTAL || s2 == SpecialType.STRIPED_VERTICAL)) ||
                    (s2 == SpecialType.WRAPPED_BOMB && (s1 == SpecialType.STRIPED_HORIZONTAL || s1 == SpecialType.STRIPED_VERTICAL)) -> {
                onSound("bomb", 4)
                showComboPopup("MEGA CROSS BLAST!", c2.toFloat(), r2.toFloat())
                for (dr in -1..1) {
                    if (r2 + dr in 0 until rows) clearRow(r2 + dr)
                }
                for (dc in -1..1) {
                    if (c2 + dc in 0 until cols) clearCol(c2 + dc)
                }
                delay(30)
            }

            // Wrapped + Wrapped -> 5x5 explosion
            s1 == SpecialType.WRAPPED_BOMB && s2 == SpecialType.WRAPPED_BOMB -> {
                onSound("bomb", 4)
                showComboPopup("GIGA BOMB!", c2.toFloat(), r2.toFloat())
                for (r in (r2 - 2)..(r2 + 2)) {
                    for (c in (c2 - 2)..(c2 + 2)) {
                        if (r in 0 until rows && c in 0 until cols) {
                            clearTile(r, c)
                        }
                    }
                }
                delay(30)
            }
        }
    }

    private suspend fun resolveCascades(initialCombo: Int, onStateUpdate: (MatchEngineState) -> Unit) {
        var combo = initialCombo

        var hasEmptySpaces = false
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (r to c !in emptyCells && board[r][c] == null) {
                    hasEmptySpaces = true
                }
            }
        }
        if (hasEmptySpaces) {
            applyGravity()
            onStateUpdate(getState())
            delay(30)
            spawnNewCandies()
            onStateUpdate(getState())
            delay(40)
            checkCollectedIngredients()
        }
        while (true) {
            val matches = findMatches()
            if (matches.isEmpty()) break

            val comboMessage = when (combo) {
                1 -> "SWEET!"
                2 -> "TASTY!"
                3 -> "DELICIOUS!"
                4 -> "SUGAR CRUSH!"
                else -> "DIVINE!"
            }
            lastComboMessage = comboMessage
            onSound("match", combo)
            onHaptic()

            // Process matches, create special candies
            val specialCreations = calculateSpecialCreations(matches)
            val allMatchedTiles = matches.flatten().toSet()

            for (tilePos in allMatchedTiles) {
                val (r, c) = tilePos
                val tile = board[r][c]
                if (tile != null) {
                    spawnParticles(c.toFloat(), r.toFloat(), tile.color.baseColor)
                    // Trigger tile's special if it had one
                    if (tile.special != SpecialType.NONE && tilePos !in specialCreations.keys) {
                        detonateSpecial(r, c, tile.special)
                    }
                    // Damage obstacles under/adjacent
                    damageObstaclesAt(r, c)
                    // Update goal if color collection
                    updateColorGoal(tile.color)
                    clearTile(r, c)
                }
            }

            // Add created special candies
            for ((pos, special) in specialCreations) {
                val (r, c) = pos
                val color = CandyColor.random(config.colorCount)
                board[r][c] = CandyTile(
                    id = UUID.randomUUID().toString(),
                    row = r,
                    col = c,
                    color = color,
                    special = special
                )
            }

            val pointsEarned = allMatchedTiles.size * 60 * combo
            score += pointsEarned
            updateScoreGoal(score)
            showComboPopup("+$pointsEarned", 4f, 4f)

            onStateUpdate(getState())
            delay(40)

            // Apply gravity and drop
            applyGravity()
            onStateUpdate(getState())
            delay(30)

            // Spawn new candies at top
            spawnNewCandies()
            onStateUpdate(getState())
            delay(40)

            // Check collected ingredients at bottom
            checkCollectedIngredients()

            combo++
        }

        // Clean combo text after cascades settle
        lastComboMessage = null
        if (!hasValidMoves() && !isGameOver && !isGameWon) {
            shuffleBoard()
            showComboPopup("SHUFFLING!", 4f, 4f)
            onStateUpdate(getState())
        }
    }

    private fun calculateSpecialCreations(matches: List<List<Pair<Int, Int>>>): Map<Pair<Int, Int>, SpecialType> {
        val creations = mutableMapOf<Pair<Int, Int>, SpecialType>()
        for (match in matches) {
            if (match.size >= 5) {
                val isLine = (match.map { it.first }.distinct().size == 1) || (match.map { it.second }.distinct().size == 1)
                if (isLine) {
                    val center = match[match.size / 2]
                    creations[center] = SpecialType.COLOR_BOMB
                } else {
                    val center = match[match.size / 2]
                    creations[center] = SpecialType.WRAPPED_BOMB
                }
            } else if (match.size == 4) {
                val center = match[1]
                val isHorizontal = match[0].first == match[1].first
                creations[center] = if (isHorizontal) SpecialType.STRIPED_VERTICAL else SpecialType.STRIPED_HORIZONTAL
            }
        }
        return creations
    }

    private fun findMatches(): List<List<Pair<Int, Int>>> {
        val matches = mutableListOf<List<Pair<Int, Int>>>()

        // Horizontal matches
        for (r in 0 until rows) {
            var matchLength = 1
            for (c in 0 until cols) {
                val current = board[r][c]
                val next = if (c + 1 < cols) board[r][c + 1] else null
                if (current != null && !current.isIngredient && next != null && !next.isIngredient && current.color == next.color) {
                    matchLength++
                } else {
                    if (matchLength >= 3) {
                        val currentMatch = mutableListOf<Pair<Int, Int>>()
                        for (i in 0 until matchLength) {
                            currentMatch.add(r to (c - i))
                        }
                        matches.add(currentMatch)
                    }
                    matchLength = 1
                }
            }
        }

        // Vertical matches
        for (c in 0 until cols) {
            var matchLength = 1
            for (r in 0 until rows) {
                val current = board[r][c]
                val next = if (r + 1 < rows) board[r + 1][c] else null
                if (current != null && !current.isIngredient && next != null && !next.isIngredient && current.color == next.color) {
                    matchLength++
                } else {
                    if (matchLength >= 3) {
                        val currentMatch = mutableListOf<Pair<Int, Int>>()
                        for (i in 0 until matchLength) {
                            currentMatch.add((r - i) to c)
                        }
                        matches.add(currentMatch)
                    }
                    matchLength = 1
                }
            }
        }

        return matches
    }

    private fun clearTile(r: Int, c: Int) {
        if (r in 0 until rows && c in 0 until cols) {
            board[r][c] = null
        }
    }

    private fun clearRow(r: Int) {
        for (c in 0 until cols) {
            damageObstaclesAt(r, c)
            clearTile(r, c)
        }
    }

    private fun clearCol(c: Int) {
        for (r in 0 until rows) {
            damageObstaclesAt(r, c)
            clearTile(r, c)
        }
    }

    private fun detonateSpecial(r: Int, c: Int, special: SpecialType) {
        when (special) {
            SpecialType.STRIPED_HORIZONTAL -> {
                onSound("line", 1)
                clearRow(r)
            }
            SpecialType.STRIPED_VERTICAL -> {
                onSound("line", 1)
                clearCol(c)
            }
            SpecialType.WRAPPED_BOMB -> {
                onSound("bomb", 2)
                for (dr in -1..1) {
                    for (dc in -1..1) {
                        val nr = r + dr
                        val nc = c + dc
                        if (nr in 0 until rows && nc in 0 until cols) {
                            damageObstaclesAt(nr, nc)
                            clearTile(nr, nc)
                        }
                    }
                }
            }
            SpecialType.COLOR_BOMB -> {
                onSound("color_bomb", 2)
                val randomColor = CandyColor.random(config.colorCount)
                for (rr in 0 until rows) {
                    for (cc in 0 until cols) {
                        if (board[rr][cc]?.color == randomColor) {
                            damageObstaclesAt(rr, cc)
                            clearTile(rr, cc)
                        }
                    }
                }
            }
            SpecialType.NONE -> {}
        }
    }

    private fun damageObstaclesAt(r: Int, c: Int) {
        // Direct damage on tile (e.g. Jelly)
        val direct = obstacles[r to c]
        if (direct != null) {
            when (direct) {
                TileObstacle.JELLY_DOUBLE -> {
                    obstacles[r to c] = TileObstacle.JELLY_SINGLE
                }
                TileObstacle.JELLY_SINGLE -> {
                    obstacles.remove(r to c)
                    updateGoal(GoalType.CLEAR_JELLY, 1)
                }
                TileObstacle.CHOCOLATE_BLOCK, TileObstacle.COOKIE_BLOCK -> {
                    obstacles.remove(r to c)
                    updateGoal(GoalType.CLEAR_CHOCOLATE, 1)
                }
                TileObstacle.NONE -> {}
            }
        }

        // Adjacent damage for chocolate/cookie blockers
        val adjOffsets = listOf(-1 to 0, 1 to 0, 0 to -1, 0 to 1)
        for ((dr, dc) in adjOffsets) {
            val nr = r + dr
            val nc = c + dc
            val adjObstacle = obstacles[nr to nc]
            if (adjObstacle == TileObstacle.CHOCOLATE_BLOCK || adjObstacle == TileObstacle.COOKIE_BLOCK) {
                obstacles.remove(nr to nc)
                updateGoal(GoalType.CLEAR_CHOCOLATE, 1)
                spawnParticles(nc.toFloat(), nr.toFloat(), androidx.compose.ui.graphics.Color(0xFF795548))
            }
        }
    }

    private fun applyGravity() {
        for (c in 0 until cols) {
            for (r in (rows - 1) downTo 0) {
                if (r to c in emptyCells) continue
                if (board[r][c] == null) {
                    // Find highest non-null candy above
                    for (k in (r - 1) downTo 0) {
                        if (k to c in emptyCells) continue
                        if (board[k][c] != null) {
                            board[r][c] = board[k][c]?.copy(row = r, col = c)
                            board[k][c] = null
                            break
                        }
                    }
                }
            }
        }
    }

    private fun spawnNewCandies() {
        for (c in 0 until cols) {
            for (r in 0 until rows) {
                if (r to c !in emptyCells && board[r][c] == null) {
                    val isIngredientDrop = config.ingredientSpawnCols.contains(c) && Random.nextFloat() < 0.15f
                    board[r][c] = CandyTile(
                        id = UUID.randomUUID().toString(),
                        row = r,
                        col = c,
                        color = CandyColor.random(config.colorCount),
                        isIngredient = isIngredientDrop
                    )
                }
            }
        }
    }

    private fun checkCollectedIngredients() {
        val bottomRow = rows - 1
        for (c in 0 until cols) {
            val tile = board[bottomRow][c]
            if (tile != null && tile.isIngredient) {
                board[bottomRow][c] = null
                updateGoal(GoalType.COLLECT_INGREDIENTS, 1)
                onSound("win", 1)
                showComboPopup("INGREDIENT COLLECTED!", c.toFloat(), bottomRow.toFloat())
            }
        }
    }

    private fun updateGoal(type: GoalType, amount: Int = 1) {
        val goal = goals.find { it.type == type }
        if (goal != null) {
            goal.currentAmount = minOf(goal.targetAmount, goal.currentAmount + amount)
        }
    }

    private fun updateColorGoal(color: CandyColor) {
        val goal = goals.find { it.type == GoalType.COLLECT_COLOR && it.targetColor == color }
        if (goal != null) {
            goal.currentAmount = minOf(goal.targetAmount, goal.currentAmount + 1)
        }
    }

    private fun updateScoreGoal(currentScore: Int) {
        val goal = goals.find { it.type == GoalType.SCORE }
        if (goal != null) {
            goal.currentAmount = currentScore
        }
    }

    private fun addScore(points: Int, x: Float, y: Float) {
        score += points
        updateScoreGoal(score)
        showComboPopup("+$points", x, y)
    }

    private fun showComboPopup(text: String, x: Float, y: Float) {
        val popup = ScorePopup(
            id = System.currentTimeMillis() + Random.nextLong(1000),
            text = text,
            x = x,
            y = y,
            color = androidx.compose.ui.graphics.Color(0xFFFFEE55)
        )
        scorePopups.add(popup)
        if (scorePopups.size > 5) scorePopups.removeAt(0)
    }

    private fun spawnParticles(x: Float, y: Float, color: androidx.compose.ui.graphics.Color) {
        for (i in 0..6) {
            val vx = (Random.nextFloat() - 0.5f) * 8f
            val vy = (Random.nextFloat() - 0.8f) * 8f
            particles.add(
                Particle(
                    id = System.currentTimeMillis() + Random.nextLong(10000),
                    x = x,
                    y = y,
                    vx = vx,
                    vy = vy,
                    color = color,
                    size = Random.nextFloat() * 6f + 4f
                )
            )
        }
        if (particles.size > 40) {
            particles.subList(0, particles.size - 40).clear()
        }
    }

    private fun checkLevelStatus() {
        val allGoalsCompleted = goals.all { it.isCompleted }
        if (allGoalsCompleted) {
            isGameWon = true
            onSound("win", 1)
            onHaptic()
        } else if (movesLeft <= 0) {
            isGameOver = true
            onSound("game_over", 1)
        }
    }

    fun addExtraMoves(extra: Int) {
        movesLeft += extra
        isGameOver = false
    }

    private suspend fun handleBoosterUse(r: Int, c: Int, onStateUpdate: (MatchEngineState) -> Unit) {
        val booster = activeBooster ?: return
        activeBooster = null
        isBusy = true
        when (booster) {
            "hammer" -> {
                onSound("bomb", 3)
                onHaptic()
                showComboPopup("HAMMER SMASH!", c.toFloat(), r.toFloat())
                damageObstaclesAt(r, c)
                clearTile(r, c)
                resolveCascades(1, onStateUpdate)
            }
            "bomb" -> {
                onSound("color_bomb", 3)
                onHaptic()
                showComboPopup("RAINBOW BOMB!", c.toFloat(), r.toFloat())
                board[r][c] = CandyTile(
                    id = UUID.randomUUID().toString(),
                    row = r,
                    col = c,
                    color = CandyColor.RED,
                    special = SpecialType.COLOR_BOMB
                )
            }
        }
        checkLevelStatus()
        isBusy = false
        onStateUpdate(getState())
    }

    private fun hasValidMoves(): Boolean {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (board[r][c] == null) continue
                // Test swap right
                if (c + 1 < cols && board[r][c + 1] != null) {
                    if (checkVirtualSwap(r, c, r, c + 1)) return true
                }
                // Test swap down
                if (r + 1 < rows && board[r + 1][c] != null) {
                    if (checkVirtualSwap(r, c, r + 1, c)) return true
                }
            }
        }
        return false
    }

    private fun checkVirtualSwap(r1: Int, c1: Int, r2: Int, c2: Int): Boolean {
        val t1 = board[r1][c1] ?: return false
        val t2 = board[r2][c2] ?: return false
        if (t1.special == SpecialType.COLOR_BOMB || t2.special == SpecialType.COLOR_BOMB) return true
        if (t1.special != SpecialType.NONE && t2.special != SpecialType.NONE) return true

        // Swap temporarily
        board[r1][c1] = t2
        board[r2][c2] = t1
        val hasMatch = findMatches().isNotEmpty()
        board[r1][c1] = t1
        board[r2][c2] = t2
        return hasMatch
    }

    fun findHintMove(): Pair<Pair<Int, Int>, Pair<Int, Int>>? {
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (board[r][c] == null) continue
                if (c + 1 < cols && board[r][c + 1] != null && checkVirtualSwap(r, c, r, c + 1)) {
                    return (r to c) to (r to c + 1)
                }
                if (r + 1 < rows && board[r + 1][c] != null && checkVirtualSwap(r, c, r + 1, c)) {
                    return (r to c) to (r + 1 to c)
                }
            }
        }
        return null
    }

    private fun shuffleBoard() {
        val candies = mutableListOf<CandyTile>()
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                board[r][c]?.let { candies.add(it) }
            }
        }
        candies.shuffle()
        var idx = 0
        for (r in 0 until rows) {
            for (c in 0 until cols) {
                if (r to c !in emptyCells && idx < candies.size) {
                    val tile = candies[idx++]
                    board[r][c] = tile.copy(row = r, col = c)
                }
            }
        }
        clearInitialMatches()
    }
}
