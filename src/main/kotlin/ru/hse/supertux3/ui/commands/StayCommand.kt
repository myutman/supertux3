package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model

class StayCommand(val model: Model) : Command {
    override fun execute() {
        model.afterAction()
    }
}
