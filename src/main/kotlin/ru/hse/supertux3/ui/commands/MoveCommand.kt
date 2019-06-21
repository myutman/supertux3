package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.logic.Model

/**
 * Command that represents moving between two adjacent cells inside one floor. Changes game state.
 */
class MoveCommand(private val model: Model, private val direction: Direction) : Command {
    override fun execute() {
        model.move(direction)
    }
}