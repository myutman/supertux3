package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.logic.items.WearableType

class TakeOffCommand(val model: Model, val type: WearableType) : Command {
    override fun execute() {
        model.putOff(type)
    }
}
