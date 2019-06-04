package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.logic.items.Wearable
import ru.hse.supertux3.ui.readChar
import java.lang.RuntimeException

class PutOnCommand(val model: Model, val index: Int): Command {
    override fun execute() {
        model.putOn(index)
    }
}