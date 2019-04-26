package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import org.jline.terminal.Attributes
import org.jline.terminal.TerminalBuilder
import org.jline.utils.NonBlockingReader
import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.ui.commands.*
import java.io.File

var reader: NonBlockingReader? = null

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

fun clearScreen() {
    print("\u001Bc")
}

fun main() {
    println("Welcome to Super Tux 3!")

    val state = try {
        requestGameState()
    } catch (e: Exception) {
        println("Error while loading from file! Are you sure file exists?")
        return
    }

    val attributes = Attributes()
    attributes.setLocalFlag(Attributes.LocalFlag.ECHO, false)

    val terminal = TerminalBuilder.builder()
        .jna(true)
        .system(true)
        .attributes(attributes)
        .build()

    terminal.enterRawMode()

    reader = terminal.reader()

    val visual = TermColors(TermColors.Level.TRUECOLOR)

    val model = Model(state)
    val view = View(state, visual)
    model.view = view

    val invoker = Invoker()
    val saveName = "GG"

    visual.run {

        while (!state.isGameFinished()) {

            invoker.currentCommand = when (readChar()) {
                'q' -> QuitCommand(state, saveName)
                'w' -> MoveCommand(model, Direction.UP)
                'a' -> MoveCommand(model, Direction.LEFT)
                'd' -> MoveCommand(model, Direction.RIGHT)
                's' -> MoveCommand(model, Direction.DOWN)
                'r' -> RedrawCommand(view)
                'x' -> SelfHarmCommand(model)
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