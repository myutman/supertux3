package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.ui.readChar

class PutOffCommand(val model: Model) : Command {
    private fun message(str: String) {
        model.view.printMessage("$str\nPress ESC to continue")
        while (true) {
            if (readChar().toInt() == 27) break
        }
        model.view.redraw()
    }

    override fun execute() {
        model.view.printMessage("What do you want to put off")
        val slot = readChar()
        val equipped = model.state.player.inventory.equipped
        val unequipped = model.state.player.inventory.unequipped
        try {
            val info = model.state.player.inventory.getItemInfoBySlot(slot)
            if (!info.isEquipped) {
                message("Item is not equipped")
                return
            }
            val entry = equipped.toList()[info.index]
            entry.second.putOn(model.state.player)
            equipped.remove(entry.first)
            unequipped.add(model.state.player.inventory.inventoryCur, entry.second)
            model.view.redraw()
        } catch (e: RuntimeException) {
            message(e.message!!)
            return
        }

        model.afterAction()
    }

}
