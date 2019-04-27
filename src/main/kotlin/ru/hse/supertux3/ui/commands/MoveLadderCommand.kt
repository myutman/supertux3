package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model

class MoveLadderCommand(private val model: Model) : Command {
    override fun execute() {
        model.moveLadder()
    }
}