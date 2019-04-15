package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import org.jline.terminal.Attributes
import org.jline.terminal.TerminalBuilder
import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.logic.Model

val attributes = Attributes()

val terminal = {
    attributes.setLocalFlag(Attributes.LocalFlag.ECHO, false)
    TerminalBuilder.builder()
    .jna(true)
    .system(true)
    .attributes(attributes)
    .build()
}()

val reader = {
    terminal.enterRawMode()
    terminal.reader()
}()

fun readChar(): Char {
    val buffer = CharArray(4)
    var read = 0
    while (read == 0) {
        read = reader.read(buffer)
    }
    return buffer[0]
}

fun clearScreen() {
    print("\u001Bc")
}

fun main() {
    println("Welcome to Super Tux 3!")

    val level = try {
        requestLevel()
    } catch (e: Exception) {
        println("Error while loading from file! Are you sure file exists?")
        return
    }

    val visual = TermColors(TermColors.Level.TRUECOLOR)

    val model = Model(level)
    val view = View(model.state, visual)
    model.view = view

    visual.run {

        while (true) {

            var quit = false
            when (readChar()) {
                'q' -> quit = true
                'w' -> model.move(Direction.UP)
                'a' -> model.move(Direction.LEFT)
                'd' -> model.move(Direction.RIGHT)
                's' -> model.move(Direction.DOWN)
                'x' -> model.selfHarm()
                ' ' -> model.moveLadder()
            }

            if (quit || model.state.player.hp == 0) {
                break
            }
        }
        clearScreen()
    }
}