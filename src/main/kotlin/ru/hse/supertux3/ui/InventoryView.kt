package ru.hse.supertux3.ui

import ru.hse.supertux3.logic.items.Inventory
import ru.hse.supertux3.logic.items.Wearable
import ru.hse.supertux3.logic.items.WearableType

class InventoryView(private val view: View) {
    private fun message(str: String) {
        view.printMessage("$str${System.lineSeparator()}Press ESC to continue")
        while (true) {
            if (readChar().toInt() == 27) break
        }
        view.redraw()
    }

    fun getSlotToPutOn(inventory: Inventory, slotChar: Char): Int {
        val equipped = inventory.equipped
        val unequipped = inventory.unequipped
        val info = try {
            inventory.getItemInfoBySlot(slotChar)
        } catch (e: RuntimeException) {
            message(e.message!!)
            return -1
        }
        if (info.isEquipped) {
            message("Item is already equipped")
            return -1
        }
        val item = unequipped[info.index]
        if (item !is Wearable) {
            message("Item is not wearable")
            return -1
        }
        if (equipped.containsKey(item.type)) {
            message("${item.type} is already equipped")
            return -1
        }
        return info.index
    }

    fun getSlotToTakeOff(inventory: Inventory, slotChar: Char): WearableType? {
        val equipped = inventory.equipped
        val entry = try {
            val info = inventory.getItemInfoBySlot(slotChar)
            if (!info.isEquipped) {
                message("Item is not equipped")
                return null
            }
            equipped.toList()[info.index]
        } catch (e: RuntimeException) {
            message(e.message!!)
            return null
        }
        return entry.first
    }
}