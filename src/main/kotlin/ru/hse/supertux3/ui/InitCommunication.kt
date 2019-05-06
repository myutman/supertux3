package ru.hse.supertux3.ui

import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.levels.LevelLoader

/**
 * Util place for functions to call before game starts.
 */

fun requestLevel(): Level {
    println("Do you want to load level from file? " +
            "If yes, write name of file. " +
            "If no, press Enter.")

    val file = readLine()
    val levelLoader = LevelLoader()

    val level = if (file.isNullOrEmpty()) {
        println("Started level generating!")
        levelLoader.generateLevel()
    } else {
        println("Fetching level from file!")
        levelLoader.loadLevel(file)
    }
    Thread.sleep(1500)

    return level
}