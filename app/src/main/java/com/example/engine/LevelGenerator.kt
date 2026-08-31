package com.example.engine

import com.example.data.model.CandyColor
import com.example.data.model.GoalType
import com.example.data.model.LevelConfig
import com.example.data.model.LevelGoal
import com.example.data.model.TileObstacle
import kotlin.random.Random

object LevelGenerator {

    val WORLDS = listOf(
        "Sugar Valley" to "A gentle paradise of shimmering glass gumdrops",
        "Frosting Glade" to "Icy frosted plains with delicious frozen jelly",
        "Choco Highlands" to "Rugged mountains with crunchy chocolate cookies",
        "Berry Drops Bay" to "Golden waterfalls where sweet cherries fall",
        "Crystal Nebula" to "Glowing cosmic realms of prismatic glass crystals",
        "Rainbow Spire" to "The towering peak of magical color bombs",
        "Cosmic Confection" to "The ultimate match-3 confectionery dimension"
    )

    fun getWorldInfoForLevel(levelNumber: Int): Pair<String, Int> {
        return when {
            levelNumber <= 50 -> "Sugar Valley" to 1
            levelNumber <= 100 -> "Frosting Glade" to 2
            levelNumber <= 150 -> "Choco Highlands" to 3
            levelNumber <= 200 -> "Berry Drops Bay" to 4
            levelNumber <= 250 -> "Crystal Nebula" to 5
            levelNumber <= 300 -> "Rainbow Spire" to 6
            else -> "Cosmic Confection" to 7
        }
    }

    fun getLevel(levelNumber: Int): LevelConfig {
        val (worldName, worldIndex) = getWorldInfoForLevel(levelNumber)
        val seed = levelNumber * 10007L + 42L
        val random = Random(seed)

        val rows = 8
        val cols = 8

        // Move calculation: much more generous to make game moderate instead of impossible
        val baseMoves = when {
            levelNumber <= 5 -> 35
            levelNumber <= 15 -> 30
            levelNumber <= 30 -> 26
            levelNumber <= 60 -> 24
            levelNumber <= 100 -> 22
            levelNumber <= 200 -> 20
            levelNumber <= 300 -> 18
            else -> 16
        } + (random.nextInt(3) - 1)


        val colorCount = when {
            levelNumber <= 8 -> 4
            levelNumber <= 40 -> 5
            else -> if (levelNumber % 3 == 0) 6 else 5
        }

        val emptyTiles = mutableSetOf<Pair<Int, Int>>()
        val obstacles = mutableMapOf<Pair<Int, Int>, TileObstacle>()
        val goals = mutableListOf<LevelGoal>()
        var ingredientCols = emptyList<Int>()
        var maxIngredients = 0

        // Star target score scaling
        val baseTarget = 1500 + levelNumber * 450 + (levelNumber * levelNumber * 15)
        val target1 = (baseTarget * 0.7).toInt()
        val target2 = (baseTarget * 1.3).toInt()
        val target3 = (baseTarget * 2.0).toInt()

        when (worldIndex) {
            1 -> {
                // World 1: Sugar Valley (Introduction to mechanics)
                if (levelNumber <= 5) {
                    // Simple score and color collection
                    val targetColor = CandyColor.entries[levelNumber % colorCount]
                    val colorTarget = 25 + levelNumber * 5
                    goals.add(LevelGoal(GoalType.COLLECT_COLOR, colorTarget, targetColor = targetColor))
                } else if (levelNumber <= 10) {
                    val color1 = CandyColor.entries[levelNumber % colorCount]
                    val color2 = CandyColor.entries[(levelNumber + 2) % colorCount]
                    goals.add(LevelGoal(GoalType.COLLECT_COLOR, 30 + levelNumber * 2, targetColor = color1))
                    goals.add(LevelGoal(GoalType.COLLECT_COLOR, 30 + levelNumber * 2, targetColor = color2))
                } else {
                    // Introduce light single jelly
                    val jellyCount = 8 + (levelNumber - 10) * 2
                    val chosenPositions = mutableListOf<Pair<Int, Int>>()
                    for (r in 2..5) {
                        for (c in 2..5) {
                            chosenPositions.add(r to c)
                        }
                    }
                    chosenPositions.shuffle(random)
                    chosenPositions.take(jellyCount).forEach { pos ->
                        obstacles[pos] = TileObstacle.JELLY_SINGLE
                    }
                    goals.add(LevelGoal(GoalType.CLEAR_JELLY, obstacles.size))
                }
            }

            2 -> {
                // World 2: Frosting Glade (Jelly focused with double layers)
                val isDouble = levelNumber > 30
                for (r in 1..6) {
                    for (c in 1..6) {
                        if ((r + c + levelNumber) % 2 == 0 || (r in 2..5 && c in 2..5)) {
                            obstacles[r to c] = if (isDouble && (r in 2..5 && c in 2..5)) TileObstacle.JELLY_DOUBLE else TileObstacle.JELLY_SINGLE
                        }
                    }
                }
                // Custom cutouts for board shapes
                if (levelNumber % 4 == 0) {
                    emptyTiles.add(0 to 0)
                    emptyTiles.add(0 to 7)
                    emptyTiles.add(7 to 0)
                    emptyTiles.add(7 to 7)
                }
                goals.add(LevelGoal(GoalType.CLEAR_JELLY, obstacles.size))
                if (levelNumber % 2 == 0) {
                }
            }

            3 -> {
                // World 3: Choco Highlands (Chocolate Blockers)
                val chocoCount = Math.min(30, 6 + (levelNumber - 40) / 4)
                for (i in 0 until chocoCount) {
                    val r = 2 + (i % 4)
                    val c = 1 + (i % 6)
                    obstacles[r to c] = TileObstacle.CHOCOLATE_BLOCK
                }
                goals.add(LevelGoal(GoalType.CLEAR_CHOCOLATE, chocoCount))
                val targetColor = CandyColor.entries[(levelNumber * 3) % colorCount]
                goals.add(LevelGoal(GoalType.COLLECT_COLOR, 40 + levelNumber, targetColor = targetColor))
            }

            4 -> {
                // World 4: Berry Drops Bay (Ingredients)
                ingredientCols = listOf(1, 3, 5, 6).shuffled(random).take(2)
                maxIngredients = Math.min(8, 2 + (levelNumber - 60) / 10)
                goals.add(LevelGoal(GoalType.COLLECT_INGREDIENTS, maxIngredients))
                // Add some blockers in between
                obstacles[3 to 2] = TileObstacle.COOKIE_BLOCK
                obstacles[3 to 5] = TileObstacle.COOKIE_BLOCK
                obstacles[4 to 2] = TileObstacle.COOKIE_BLOCK
                obstacles[4 to 5] = TileObstacle.COOKIE_BLOCK
            }

            5 -> {
                // World 5: Crystal Nebula (Complex shapes + Jelly + Chocolate)
                // Hourglass or diamond cutouts
                if (levelNumber % 2 == 1) {
                    emptyTiles.addAll(listOf(0 to 0, 0 to 1, 0 to 6, 0 to 7, 7 to 0, 7 to 1, 7 to 6, 7 to 7))
                } else {
                    emptyTiles.addAll(listOf(3 to 3, 3 to 4, 4 to 3, 4 to 4))
                }
                for (r in 0 until rows) {
                    for (c in 0 until cols) {
                        if (r to c !in emptyTiles) {
                            if ((r + c + levelNumber) % 3 == 0) {
                                obstacles[r to c] = TileObstacle.JELLY_DOUBLE
                            } else if (r == 4 && (c == 1 || c == 6)) {
                                obstacles[r to c] = TileObstacle.CHOCOLATE_BLOCK
                            }
                        }
                    }
                }
                val jellyCount = obstacles.count { it.value == TileObstacle.JELLY_DOUBLE || it.value == TileObstacle.JELLY_SINGLE }
                if (jellyCount > 0) goals.add(LevelGoal(GoalType.CLEAR_JELLY, jellyCount))
                val chocoCount = obstacles.count { it.value == TileObstacle.CHOCOLATE_BLOCK }
                if (chocoCount > 0) goals.add(LevelGoal(GoalType.CLEAR_CHOCOLATE, chocoCount))
            }

            6 -> {
                // World 6: Rainbow Spire (Dual goals with high targets)
                ingredientCols = listOf(2, 5)
                maxIngredients = 3
                goals.add(LevelGoal(GoalType.COLLECT_INGREDIENTS, maxIngredients))
                for (r in 5..7) {
                    for (c in 1..6) {
                        obstacles[r to c] = TileObstacle.JELLY_DOUBLE
                    }
                }
                goals.add(LevelGoal(GoalType.CLEAR_JELLY, obstacles.size))
            }

            else -> {
                // World 7: Cosmic Confection (Grand Master Levels)
                if (levelNumber % 3 == 0) {
                    ingredientCols = listOf(1, 4, 6)
                    maxIngredients = 4
                    goals.add(LevelGoal(GoalType.COLLECT_INGREDIENTS, maxIngredients))
                }
                for (r in 1..6) {
                    for (c in 1..6) {
                        if (random.nextFloat() < 0.6f) {
                            obstacles[r to c] = if (random.nextBoolean()) TileObstacle.JELLY_DOUBLE else TileObstacle.CHOCOLATE_BLOCK
                        }
                    }
                }
                val jellyCount = obstacles.count { it.value == TileObstacle.JELLY_DOUBLE || it.value == TileObstacle.JELLY_SINGLE }
                val chocoCount = obstacles.count { it.value == TileObstacle.CHOCOLATE_BLOCK }
                if (jellyCount > 0) goals.add(LevelGoal(GoalType.CLEAR_JELLY, jellyCount))
                if (chocoCount > 0) goals.add(LevelGoal(GoalType.CLEAR_CHOCOLATE, chocoCount))
            }
        }

        // Fallback if no goals added
        if (goals.isEmpty()) {
            goals.add(LevelGoal(GoalType.SCORE, target1))
        }

        return LevelConfig(
            levelNumber = levelNumber,
            worldName = worldName,
            worldIndex = worldIndex,
            rows = rows,
            cols = cols,
            maxMoves = baseMoves,
            colorCount = colorCount,
            targetScore1Star = target1,
            targetScore2Star = target2,
            targetScore3Star = target3,
            goals = goals,
            emptyTiles = emptyTiles,
            initialObstacles = obstacles,
            ingredientSpawnCols = ingredientCols,
            maxIngredientsOnBoard = maxIngredients
        )
    }
}
