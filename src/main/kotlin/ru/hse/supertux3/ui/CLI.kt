package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import org.jline.terminal.Attributes
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.ui.commands.*
import java.io.File
import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import ru.hse.supertux3.SuperTux3Grpc
import ru.hse.supertux3.SuperTux3Proto
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.GameState
import java.util.concurrent.TimeUnit


val saveName = ".save"
var reader: NonBlockingReader? = null

/*
 * Reads key press from keyboard.
 */
fun readChar(): Char {
    val buffer = CharArray(4)
    var read = 0
    while (read == 0) {
        read = reader!!.read(buffer)
    }
    return buffer[0]
}

fun deleteSave(saveName: String) {
    File(saveName).delete()
}

/*
 * Clears screen.
 */
fun clearScreen() {
    print("\u001Bc")
}

fun main() {
    println("Welcome to Super Tux 3!")

    val attributes = Attributes()
    attributes.setLocalFlag(Attributes.LocalFlag.ECHO, false)
    attributes.setLocalFlag(Attributes.LocalFlag.ECHOKE, true)
    attributes.setLocalFlag(Attributes.LocalFlag.ECHOK, false)

    val terminal = TerminalBuilder.builder()
        .jna(true)
        .system(true)
        .attributes(attributes)
        .build()

    terminal.enterRawMode()

    reader = terminal.reader()

    val gameType = getGameType();
    if (gameType == GameType.SINGLEPLAYER) {
        processSinglePlayer(terminal)
    } else {
        processMultiPlayer(terminal)
    }
}

fun processSinglePlayer(terminal: Terminal) {
    val state = try {
        requestGameState()
    } catch (e: Exception) {
        e.printStackTrace()
        println("Error while loading from file! Are you sure file exists?")
        return
    }

    val visual = TermColors(TermColors.Level.TRUECOLOR)

    val model = Model(state)
    val view = View(state, visual, terminal)
    model.view = view

    val invoker = Invoker()

    visual.run {

        while (!state.isGameFinished()) {

            invoker.currentCommand = when (readChar()) {
                'q' -> QuitCommand(state, saveName)
                'w' -> MoveCommand(model, Direction.UP)
                'a' -> MoveCommand(model, Direction.LEFT)
                'd' -> MoveCommand(model, Direction.RIGHT)
                's' -> MoveCommand(model, Direction.DOWN)
                '.' -> StayCommand(model)
                'r' -> RedrawCommand(view)
                'x' -> SelfHarmCommand(model)
                'l' -> LootCommand(model)
                'o' -> PutOnCommand(model)
                'p' -> TakeOffCommand(model)
                'j' -> SlideUpCommand(view)
                'k' -> SlideDownCommand(view)
                '?' -> ShowItemInfoCommand(view)
                'h' -> HelpCommand(view)
                ' ' -> MoveLadderCommand(model)
                else -> null
            }

            invoker.run()

            if (state.player.isDead()) {
                deleteSave(saveName)
            }
        }
        clearScreen()
    }
}


fun processMultiPlayer(terminal: Terminal) {
    val role = getRole()

    val host = getHost()
    val port = getPort()
    val channel = ManagedChannelBuilder.forAddress(host, port).build()
    val stub = SuperTux3Grpc.newBlockingStub(channel)

    val userNumber: Int
    val level: Level

    if (role == MultiplayerRole.JOINER || role == MultiplayerRole.LOBBYIST) {
        val gameId = getId()
        println("Wait for response. It may take time.")
        val response = stub.joinGame(SuperTux3Proto.JoinGameRequest.newBuilder().setGameId(gameId).build())
        level = Level.load(response.level)
        userNumber = response.userNumber
    } else {
        // idk
        // some shit, but eventually we'll get level and userNumber
        val response = stub.joinGame(SuperTux3Proto.JoinGameRequest.newBuilder().setGameId("-0").build())
        level = Level.load(response.level)
        userNumber = response.userNumber
    }
    val player = level.players.find { player -> player.userId == userNumber }
    if (player == null) {
        println("ERROROR: server sent userId that didn't match any userId in Level.")
        return
    }

    val state = GameState(level, player)
    val visual = TermColors(TermColors.Level.TRUECOLOR)
    val view = View(state, visual, terminal)

    // start game
    // while (true)
    //     request isMyTurn
    //     if my turn, some controller logic with commands, get updates
    //

    while (true) {

        val isMyTurn = stub.isMyTurn(SuperTux3Proto.IsMyTurnRequest.newBuilder().build()).myTurn
        if (isMyTurn) {
            // some controller logic + send request
        }

        val updates = stub.getUpdate(SuperTux3Proto.GetUpdateRequest.newBuilder().build())


        //     save updates to level (maybe apply them to player)
        //     drawBeingSeen()
        //     maybe die
        break
    }

    /**
     * Some things???:
     * commands are responsible for response to server
     */

    channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
}