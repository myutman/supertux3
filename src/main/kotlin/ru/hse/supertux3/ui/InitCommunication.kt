package ru.hse.supertux3.ui

import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.levels.LevelGenerator
import java.lang.Exception


fun parseIntFromArrayOrDefault(array: Array<String>, index: Int, default: Int): Int {
    return try {
        array[index].toInt()
    } catch (e: Exception) {
        default
    }
}

fun requestLevel(d: Int, h: Int, w: Int): Level {
    println("Do you want to load level from file? " +
            "If yes, write name of file. " +
            "If no, press Enter.")

    val file = readLine()
    val level = if (file.isNullOrEmpty()) {
        println("Started level generating!")
        LevelGenerator.generate(d, h, w)
    } else {
        // load level from file
        println("Fetching level from file!")
        LevelGenerator.generate(d, h, w)
    }
    Thread.sleep(1000)
    /*println("Loading island...")
    Thread.sleep(1500)
    println("Building walls...")
    Thread.sleep(1500)
    println("Repairing doors...")
    Thread.sleep(1500)
    println("Checking ladders...")*/

    return level
}