sed -i 's/\/\/ Initialize 150 levels/\/\/ Initialize 350 levels/' app/src/main/java/com/example/data/GameRepository.kt
sed -i 's/(1..150)/(1..350)/' app/src/main/java/com/example/data/GameRepository.kt
sed -i 's/if (levelId < 150)/if (levelId < 350)/' app/src/main/java/com/example/data/GameRepository.kt
sed -i 's/(150 - highestUnlockedLevel).coerceIn(0, 149)/(350 - highestUnlockedLevel).coerceIn(0, 349)/' app/src/main/java/com/example/ui/screens/LevelMapScreen.kt
sed -i 's/or levelNum == 150)/or levelNum == 350)/' app/src/main/java/com/example/ui/screens/LevelMapScreen.kt
