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
import io.grpc.ManagedChannelBuilder
import org.jline.reader.LineReader
import org.jline.reader.LineReaderBuilder
import ru.hse.supertux3.CommandOuterClass
import ru.hse.supertux3.SuperTux3Grpc
import ru.hse.supertux3.SuperTux3Proto
import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.Floor
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.GameState
import ru.hse.supertux3.logic.mobs.Player
import java.util.concurrent.TimeUnit


val saveName = ".save"
var reader: NonBlockingReader? = null
lateinit var lineReader: LineReader

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
    lineReader = LineReaderBuilder.builder().terminal(terminal).build()

    val gameType = getGameType()
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
                'o' -> {
                    view.printMessage("What do you want to put on")
                    val slotChar = readChar()
                    val index = model.getSlotToPutOn(slotChar)
                    if (index == -1) {
                        EmptyCommand()
                    } else {
                        PutOnCommand(model, index)
                    }
                }
                'p' -> {
                    view.printMessage("What do you want to take off")
                    val slotChar = readChar()
                    val type = model.getSlotToTakeOff(slotChar)
                    if (type == null) {
                        EmptyCommand()
                    } else {
                        TakeOffCommand(model, type)
                    }
                }
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
        userNumber = response.userId
    } else {
        val gameId = getId()
        stub.createGame(SuperTux3Proto.CreateGameRequest.newBuilder().setGameId(gameId).build())

        println("Enter the same id to start game.")
        val secondGameId = getId()
        // TODO: do this better
        val response = stub.startGame(SuperTux3Proto.StartGameRequest.newBuilder().setGameId(gameId).build())
        level = Level.load(response.level)
        userNumber = response.userId
    }

    val player = level.players.find { player -> player.userId == userNumber }
    if (player == null) {
        println("ERROROR: server sent userId that didn't match any userId in Level.")
        return
    }

    val state = GameState(level, player)
    val visual = TermColors(TermColors.Level.TRUECOLOR)
    val view = View(state, visual, terminal)

    while (true) {
        val isMyTurn = stub.isMyTurn(SuperTux3Proto.IsMyTurnRequest.newBuilder().build()).myTurn
        val updatesTurn: SuperTux3Proto.Turn
        if (isMyTurn) {
            val currentCommandBuilder = CommandOuterClass.Command.newBuilder()
            when (readChar()) {
                'w' -> currentCommandBuilder.setMove(
                    CommandOuterClass.MoveCommand.newBuilder().setDirection(CommandOuterClass.Direction.UP)
                )
                'a' -> currentCommandBuilder.setMove(
                    CommandOuterClass.MoveCommand.newBuilder().setDirection(CommandOuterClass.Direction.LEFT)
                )
                's' -> currentCommandBuilder.setMove(
                    CommandOuterClass.MoveCommand.newBuilder().setDirection(CommandOuterClass.Direction.DOWN)
                )
                'd' -> currentCommandBuilder.setMove(
                    CommandOuterClass.MoveCommand.newBuilder().setDirection(CommandOuterClass.Direction.RIGHT)
                )
                /*'q' -> QuitCommand(state, saveName)
                ' ' -> MoveLadderCommand(model)
                '.' -> StayCommand(model)
                'l' -> LootCommand(model)
                'o' -> {
                    view.printMessage("What do you want to put on")
                    val slotChar = readChar()
                    val index = model.getSlotToPutOn(slotChar)
                    if (index == -1) {
                        EmptyCommand()
                    } else {
                        PutOnCommand(model, index)
                    }
                }
                'p' -> {
                    view.printMessage("What do you want to take off")
                    val slotChar = readChar()
                    val type = model.getSlotToTakeOff(slotChar)
                    if (type == null) {
                        EmptyCommand()
                    } else {
                        TakeOffCommand(model, type)
                    }
                }*/

                'r' -> RedrawCommand(view)
                'j' -> SlideUpCommand(view)
                'k' -> SlideDownCommand(view)
                '?' -> ShowItemInfoCommand(view)
                'h' -> HelpCommand(view)
                else -> null
            }
            val command = currentCommandBuilder.build()
            val turnResponse = stub.makeTurn(SuperTux3Proto.MakeTurnRequest.newBuilder().setCommand(command).build())
            updatesTurn = turnResponse.turn
        } else {
            updatesTurn = stub.getUpdate(SuperTux3Proto.GetUpdateRequest.newBuilder().build()).turn
        }

        val cellsList = ArrayList<Cell>()
        for (proto in updatesTurn.cellsList) {
            val cell = Level.loadCell(state.level, proto)

            // check if our player; if yes then update player
            if (cell is Floor && cell.stander is Player) {
                val cellPlayer = cell.stander as Player
                if (cellPlayer.userId == userNumber) {
                    state.player.copyFrom(cellPlayer)
                    // TODO: inventory
                }
            }

            level.setCell(cell.coordinates, cell)
            cellsList.add(cell)
        }

        view.lazyRedraw(cellsList)

        if (state.player.isDead()) {
            view.died()
            break
        }
    }

    channel.shutdown().awaitTermination(3, TimeUnit.SECONDS)
}