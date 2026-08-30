package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class CandyColor(val displayName: String, val baseColor: Color, val glowColor: Color, val accentColor: Color) {
    RED("Strawberry", Color(0xFFFF2E63), Color(0xFFFF7597), Color(0xFFFFD1DC)),
    ORANGE("Tangerine", Color(0xFFFF7A00), Color(0xFFFFA94D), Color(0xFFFFE8CC)),
    YELLOW("Lemon", Color(0xFFFFD600), Color(0xFFFFF066), Color(0xFFFFFFB3)),
    GREEN("Lime", Color(0xFF00E676), Color(0xFF69F0AE), Color(0xFFB9F6CA)),
    BLUE("Berry", Color(0xFF00B0FF), Color(0xFF80D8FF), Color(0xFFE1F5FE)),
    PURPLE("Grape", Color(0xFFD500F9), Color(0xFFEA80FC), Color(0xFFF3E5F5));

    companion object {
        fun random(count: Int = 6): CandyColor {
            val values = entries.take(count.coerceIn(4, 6))
            return values.random()
        }
    }
}

enum class SpecialType {
    NONE,
    STRIPED_HORIZONTAL,
    STRIPED_VERTICAL,
    WRAPPED_BOMB,
    COLOR_BOMB
}

enum class TileObstacle {
    NONE,
    JELLY_SINGLE,
    JELLY_DOUBLE,
    CHOCOLATE_BLOCK,
    COOKIE_BLOCK
}

enum class GoalType(val label: String) {
    SCORE("Reach Score"),
    CLEAR_JELLY("Clear Frosting"),
    CLEAR_CHOCOLATE("Break Chocolate"),
    COLLECT_INGREDIENTS("Bring Ingredients Down"),
    COLLECT_COLOR("Collect Candies")
}

data class LevelGoal(
    val type: GoalType,
    val targetAmount: Int,
    var currentAmount: Int = 0,
    val targetColor: CandyColor? = null
) {
    val isCompleted: Boolean
        get() = currentAmount >= targetAmount

    val progressFraction: Float
        get() = if (targetAmount == 0) 1f else (currentAmount.toFloat() / targetAmount.toFloat()).coerceIn(0f, 1f)
}

data class CandyTile(
    val id: String,
    val row: Int,
    val col: Int,
    val color: CandyColor,
    val special: SpecialType = SpecialType.NONE,
    val obstacle: TileObstacle = TileObstacle.NONE,
    val isIngredient: Boolean = false,
    val isMatched: Boolean = false,
    val isHinted: Boolean = false,
    val isSelected: Boolean = false
)

enum class BoosterType(val displayName: String, val description: String, val iconRes: String, val coinCost: Int) {
    HAMMER("Lollipop Hammer", "Smash any candy or blocker instantly!", "ic_hammer", 80),
    FREE_SWAP("Free Hand Swap", "Swap any two candies without using a move!", "ic_swap", 100),
    COLOR_BOMB("Color Bomb Start", "Spawn a sparkling rainbow bomb on board!", "ic_bomb", 120),
    EXTRA_MOVES("+5 Extra Moves", "Add +5 moves to finish the level!", "ic_moves", 60)
}

data class LevelConfig(
    val levelNumber: Int,
    val worldName: String,
    val worldIndex: Int,
    val rows: Int = 8,
    val cols: Int = 8,
    val maxMoves: Int = 25,
    val colorCount: Int = 5,
    val targetScore1Star: Int,
    val targetScore2Star: Int,
    val targetScore3Star: Int,
    val goals: List<LevelGoal>,
    val emptyTiles: Set<Pair<Int, Int>> = emptySet(),
    val initialObstacles: Map<Pair<Int, Int>, TileObstacle> = emptyMap(),
    val ingredientSpawnCols: List<Int> = emptyList(),
    val maxIngredientsOnBoard: Int = 0
)

data class ScorePopup(
    val id: Long,
    val text: String,
    val x: Float,
    val y: Float,
    val color: Color
)

data class Particle(
    val id: Long,
    val x: Float,
    val y: Float,
    val vx: Float,
    val vy: Float,
    val color: Color,
    val size: Float,
    val alpha: Float = 1f,
    val lifetimeMs: Long = 600L,
    val createdAt: Long = System.currentTimeMillis()
)
