package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.ui.readChar

class PutOffCommand(val model: Model) : Command {
    override fun execute() {
        val c = readChar()

        model.afterAction()
    }

}
