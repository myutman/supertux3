package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import org.jline.terminal.Attributes
import org.jline.terminal.TerminalBuilder
import ru.hse.supertux3.levels.LevelGenerator


fun main() {
    val visual = TermColors(TermColors.Level.TRUECOLOR)

    val attributes = Attributes()
    //attributes.setLocalFlag(Attributes.LocalFlag.ECHOE, true)
    attributes.setLocalFlag(Attributes.LocalFlag.ECHO, false)
    val terminal = TerminalBuilder.builder()
        .jna(true)
        .system(true)
        .attributes(attributes)
        .build()
    terminal.enterRawMode()
    val reader = terminal.reader()

    val level = LevelGenerator.generate(4, 30, 40)
    println("Generated")
    val view = View(level, visual)

    visual.run {

        while (true) {

            val buffer = CharArray(4)
            var read = 0
            while (read == 0) {
                read = reader.read(buffer)
            }

            var quit = false
            when (buffer[0]) {
                'q' -> quit = true
                'w' -> view.moveUp()
                'a' -> view.moveLeft()
                'd' -> view.moveRight()
                's' -> view.moveDown()
                ' ' -> view.moveLadder()
            }

            if (quit) {
                break
            }
        }
        print("\u001Bc")
    }
}