package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model

class SelfHarmCommand(val model: Model) : Command {
    override fun execute() {
        model.selfHarm()
    }
}