package ru.hse.supertux3.levels

/**
 * Class, that can load level from file or generate new level
 */
class LevelLoader {
    /**
     * Loads level from file
     */
    fun loadLevel(fileName: String): Level {
        return Level.load(fileName)
    }

    /**
     * Generates new level with mobs
     */
    fun generateLevel(): Level {
        val depth = 4
        val heightWithWalls = 30
        val widthWithWalls = 40
        return LevelGenerator.generate(depth, heightWithWalls, widthWithWalls)
    }
}
