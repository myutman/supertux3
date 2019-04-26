package ru.hse.supertux3.ui

import ru.hse.supertux3.levels.LevelLoader
import ru.hse.supertux3.logic.GameState

/**
 * Util place for functions to call before game starts.
 */

fun requestGameState(): GameState {
    clearScreen()
    println("Do you want to load level from file? " +
            "If yes, write name of file. " +
            "If no, press Enter.")

    val file = readLine()
    val levelLoader = LevelLoader()

    val state: GameState = if (file.isNullOrEmpty()) {
        println("Started level generating!")
        val level = levelLoader.generateLevel()
        val player = level.createPlayer()
        GameState(level, player)
    } else {
        println("Fetching level from file!")
        levelLoader.loadGameState(file)
    }
    Thread.sleep(1500)

    return state
}