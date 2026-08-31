sed -i '/var combo = initialCombo/a\
\
        var hasEmptySpaces = false\
        for (r in 0 until rows) {\
            for (c in 0 until cols) {\
                if (r to c !in emptyCells \&\& board[r][c] == null) {\
                    hasEmptySpaces = true\
                }\
            }\
        }\
        if (hasEmptySpaces) {\
            applyGravity()\
            onStateUpdate(getState())\
            delay(60)\
            spawnNewCandies()\
            onStateUpdate(getState())\
            delay(70)\
            checkCollectedIngredients()\
        }' app/src/main/java/com/example/engine/Match3Engine.kt
