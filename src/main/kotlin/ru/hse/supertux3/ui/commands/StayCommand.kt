package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model

/**
 * Command that represents staying at the same place. Changes state.
 */
class StayCommand(val model: Model) : Command {
    override fun execute() {
        model.afterAction()
    }
}
