package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model

/**
 * Command that represents taking the stairs. Changes game state.
 */
class MoveLadderCommand(private val model: Model) : Command {
    override fun execute() {
        model.moveLadder()
    }
}