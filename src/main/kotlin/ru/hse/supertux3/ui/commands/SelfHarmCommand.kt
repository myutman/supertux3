package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model

/**
 * Command that represents selfharming. Only available in single player mode. Changes state.
 */
class SelfHarmCommand(val model: Model) : Command {
    override fun execute() {
        model.selfHarm()
    }
}