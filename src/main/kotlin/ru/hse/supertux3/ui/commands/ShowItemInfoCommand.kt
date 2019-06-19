package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.ui.View
import ru.hse.supertux3.ui.readChar
import java.lang.RuntimeException

/**
 * Command that represents showing item about item.
 */
class ShowItemInfoCommand(val view: View) : Command {
    override fun execute() {
        view.showInventoryMessage()
        val slot = readChar()
        view.redraw()
        val str: String = try {
            val itemInfo = view.state.player.inventory.getItemInfoBySlot(slot)
            itemInfo.item.description
        } catch (e: RuntimeException) {
            e.message!!
        }
        view.printMessage("$str${System.lineSeparator()}Press ESC to continue")
        while (true) {
            if (readChar().toInt() == 27) break
        }
        view.redraw()
    }

}
