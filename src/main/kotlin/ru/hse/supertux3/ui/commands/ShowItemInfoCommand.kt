package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.ui.View
import ru.hse.supertux3.ui.readChar

class ShowItemInfoCommand(val view: View) : Command {
    override fun execute() {
        view.showInventoryMessage()
        val c = readChar()
        view.showInfo(c)
        while (true) {
            if (readChar().toInt() == 27) break
        }
        view.redraw()
    }

}
