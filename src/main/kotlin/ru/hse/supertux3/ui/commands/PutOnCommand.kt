package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.logic.items.Wearable
import ru.hse.supertux3.ui.readChar
import java.lang.RuntimeException

class PutOnCommand(val model: Model): Command {
    private fun message(str: String) {
        model.view.printMessage("$str${System.lineSeparator()}Press ESC to continue")
        while (true) {
            if (readChar().toInt() == 27) break
        }
        model.view.redraw()
    }

    override fun execute() {
        model.view.printMessage("What do you want to put on")
        val slot = readChar()
        val equipped = model.state.player.inventory.equipped
        val unequipped = model.state.player.inventory.unequipped
        try {
            val info = model.state.player.inventory.getItemInfoBySlot(slot)
            if (info.isEquipped) {
                message("Item is already equipped")
                return
            }
            val item = unequipped[info.index]
            if (item !is Wearable) {
                message("Item is not wearable")
                return
            }
            if (equipped.containsKey(item.type)) {
                message("${item.type} is already equipped")
                return
            }
            item.putOn(model.state.player)
            unequipped.removeAt(info.index)
            equipped.put(item.type, item)
            model.view.redraw()
        } catch (e: RuntimeException) {
            message(e.message!!)
            return
        }

        model.afterAction()
    }
}