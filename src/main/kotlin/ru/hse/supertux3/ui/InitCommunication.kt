package ru.hse.supertux3.ui

import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
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
    clearScreen()

    println("Press s to play in singleplayer mode or m to start multiplayer session.")

    while (true) {
        val input = readChar()
        if (input == 'm') {
            return GameType.MULTIPLAYER
        }
        if (input == 's') {
            return GameType.SINGLEPLAYER
        }
    }
}

fun getRole(): MultiplayerRole {
    return MultiplayerRole.JOINER
}

fun getHost(): String {
    println("Enter hostname: ")
    val lineReader = LineReaderBuilder.builder().terminal(terminal).build()
    while (true) {
        val hostname: String? = lineReader.readLine()
        if (hostname != null) {
            return hostname
        }
    }
}

fun getPort(): Int {
    val lineReader = LineReaderBuilder.builder().terminal(terminal).build()
    while (true) {
        println("Enter port number: ")
        val portNumber: Int? = lineReader.readLine()?.toIntOrNull()
        if (portNumber == null || portNumber < 1000 || portNumber > 65535) {
            println("Incorrect input!")
        } else {
            return portNumber
        }
    }
}

fun getId(): String {
    println("Enter gameId: ")
    val lineReader = LineReaderBuilder.builder().terminal(terminal).build()
    while (true) {
        val id: String? = lineReader.readLine()
        if (id != null) {
            return id
        }
    }
}

