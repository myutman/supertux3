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

/**
 * Getting from user game mode(singleplayer or multiplayer)
 */
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

/**
 * Getting from user his role(creator, joiner)
 */
fun getRole(): MultiplayerRole {
    println("Press 'c' to create game, 'j' to join or l to join in lobby.")
    while (true) {
        val input = readChar()
        if (input == 'c') {
            return MultiplayerRole.CREATOR
        }
        if (input == 'j') {
            return MultiplayerRole.JOINER
        }
        if (input == 'l') {
            return MultiplayerRole.LOBBYIST
        }
    }
}

/**
 * Getting from user server host
 */
fun getHost(): String {
    println("Enter hostname: ")
    while (true) {
        val hostname: String? = lineReader.readLine()
        if (hostname.isNullOrBlank()) {
            println("127.0.0.1")
            return "127.0.0.1"
        }
        return hostname
    }
}

/**
 * Getting from user server port
 */
fun getPort(): Int {
    while (true) {
        println("Enter port number: ")
        val portNumber: Int? = lineReader.readLine()?.toIntOrNull()
        if (portNumber == null) {
            println(9805)
            return 9805
        }
        if (portNumber < 1000 || portNumber > 65535) {
            println("Incorrect input!")
        } else {
            return portNumber
        }
    }
}

/**
 * Getting from user game id
 */
fun getId(): String {
    println("Enter gameId: ")
    while (true) {
        val id: String? = lineReader.readLine()
        if (id != null) {
            return id
        }
    }
}

