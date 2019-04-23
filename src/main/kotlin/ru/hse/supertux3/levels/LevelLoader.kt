package ru.hse.supertux3.levels

import ru.hse.supertux3.logic.GameState

class LevelLoader {
    fun loadLevel(fileName: String): Level {
        return Level.load(fileName)
    }

    fun loadGameState(fileName: String): GameState {
        val level = Level.load(fileName)
        return GameState(level, level.player!!) //TODO
    }

    fun generateLevel(): Level {
        val depth = 4
        val heightWithWalls = 30
        val widthWithWalls = 40
        return LevelGenerator.generate(depth, heightWithWalls, widthWithWalls)
    }
}