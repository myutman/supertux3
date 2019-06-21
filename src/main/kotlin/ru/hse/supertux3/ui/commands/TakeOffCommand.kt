package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.logic.items.WearableType

/**
 * Command that represents taking the inventory off. Changes game state.
 */
class TakeOffCommand(val model: Model, val type: WearableType) : Command {
    override fun execute() {
        model.takeOff(type)
    }
}
