package ru.hse.supertux3.levels
import ru.hse.supertux3.logic.GameState

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
     * Loads all game state from file
     */
    fun loadGameState(fileName: String): GameState {
        val level = Level.load(fileName)
        return GameState(level, level.player!!) //TODO
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
