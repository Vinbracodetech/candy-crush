sed -i 's/levelNumber <= 20 -> "Sugar Valley" to 1/levelNumber <= 50 -> "Sugar Valley" to 1/' app/src/main/java/com/example/engine/LevelGenerator.kt
sed -i 's/levelNumber <= 40 -> "Frosting Glade" to 2/levelNumber <= 100 -> "Frosting Glade" to 2/' app/src/main/java/com/example/engine/LevelGenerator.kt
sed -i 's/levelNumber <= 60 -> "Choco Highlands" to 3/levelNumber <= 150 -> "Choco Highlands" to 3/' app/src/main/java/com/example/engine/LevelGenerator.kt
sed -i 's/levelNumber <= 80 -> "Berry Drops Bay" to 4/levelNumber <= 200 -> "Berry Drops Bay" to 4/' app/src/main/java/com/example/engine/LevelGenerator.kt
sed -i 's/levelNumber <= 100 -> "Crystal Nebula" to 5/levelNumber <= 250 -> "Crystal Nebula" to 5/' app/src/main/java/com/example/engine/LevelGenerator.kt
sed -i 's/levelNumber <= 125 -> "Rainbow Spire" to 6/levelNumber <= 300 -> "Rainbow Spire" to 6/' app/src/main/java/com/example/engine/LevelGenerator.kt

sed -i 's/val chocoCount = 6 + (levelNumber - 40) \/ 2/val chocoCount = Math.min(30, 6 + (levelNumber - 40) \/ 4)/' app/src/main/java/com/example/engine/LevelGenerator.kt
sed -i 's/maxIngredients = 2 + (levelNumber - 60) \/ 7/maxIngredients = Math.min(5, 2 + (levelNumber - 60) \/ 10)/' app/src/main/java/com/example/engine/LevelGenerator.kt

