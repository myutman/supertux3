package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.ui.View
import ru.hse.supertux3.ui.readChar

/**
 * Command that represents listing help.
 */
class HelpCommand(val view: View) : Command {
    override fun execute() {
        val str =
            """ |
                |     w
                |     |
                |   a-.-d
                |     |
                |     d
                |
                |'q' -> save and quit the game
                |' ' -> walk up or down the ladder
                |'r' -> redraw
                |'x' -> self harm (only available in single player mode)
                |'l' -> loot the drop here
                |'o' -> put on
                |'p' -> take off
                |'j' -> slide up unequipped
                |'k' -> slide down unequipped
                |'?' -> show information about item
                |'h' -> help
        """.trimMargin()
        view.printMessage("$str${System.lineSeparator()}Press ESC to continue")
        while (true) {
            if (readChar().toInt() == 27) break
        }
        view.redraw()
    }

}
