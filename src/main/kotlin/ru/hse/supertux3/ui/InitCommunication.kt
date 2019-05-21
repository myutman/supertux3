package ru.hse.supertux3.ui

import ru.hse.supertux3.levels.LevelLoader
import ru.hse.supertux3.logic.GameState
import java.io.File

/**
 * Util place for functions to call before game starts.
 */
fun requestGameState(): GameState {
    clearScreen()
    val file = File(saveName)

    println("Press n to start a new game.")

    val exists = if (file.exists()) {
        println()
        println("You have a saved game if you want to load it press l.")
        true
    } else {
        false
    }

    val levelLoader = LevelLoader()

    while (true) {
        val input = readChar()
        if (exists && input == 'l') {
            clearScreen()
            println("Fetching level from file!")
            return levelLoader.loadGameState(saveName)
        }
        if (input == 'n') {
            clearScreen()
            println("Started level generating!")
            val level = levelLoader.generateLevel()
            val player = level.createPlayer()
            return GameState(level, player)
        }
    }
}

fun getGameType(): GameType {
    // TODO

    return GameType.SINGLEPLAYER
}