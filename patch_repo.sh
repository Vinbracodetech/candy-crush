sed -i 's/levelDao.getAllProgressSync()/levelDao.getAllProgress().firstOrNull() ?: emptyList()/' app/src/main/java/com/example/data/GameRepository.kt
