package ru.hse.supertux3.levels

class LevelLoader {
    fun loadLevel(fileName: String): Level {
        return Level.load(fileName)
    }

    fun generateLevel(): Level {
        val depth = 4
        val heightWithWalls = 30
        val widthWithWalls = 40
        return LevelGenerator.generate(depth, heightWithWalls, widthWithWalls)
    }
}