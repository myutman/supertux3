package ru.hse.supertux3.levels

class LevelLoader {
    fun loadLevel(id: String, fileName: String): Level {
        return Level(1, 1, 1)
    }

    fun genetateLevel(): Level {
        val depth = 4
        val heightWithWalls = 30
        val widthWithWalls = 40
        return LevelGenerator.generate(depth, heightWithWalls, widthWithWalls)
    }
}