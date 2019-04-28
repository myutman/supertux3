package ru.hse.supertux3.logic.items

import java.lang.RuntimeException

class Inventory {
    val equipped = mutableMapOf<WearableType, Wearable>()
    val unequipped = mutableListOf<Item>()
    private var inventoryIndex = 0

    val inventoryCur: Int
        get() { return inventoryIndex }

    companion object {
        val unwornSlotNames: List<Char> = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val wornSlotNames: List<Char> = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    }

    data class ItemInfo(val item: Item, val isEquipped: Boolean, val index: Int)

    fun slideDown(): Boolean {
        if (inventoryIndex + unwornSlotNames.size < unequipped.size) {
            inventoryIndex++
            return true
        }
        return false
    }

    fun slideUp(): Boolean {
        if (inventoryIndex > 0) {
            inventoryIndex--
            return true
        }
        return false
    }


    fun getItemInfoBySlot(slot: Char): ItemInfo {
        val error = "No element in selected slot"
        if (Inventory.wornSlotNames.contains(slot)) {
            val index = Inventory.wornSlotNames.indexOf(slot)
            val equipped = equipped.toList()
            if (index >= equipped.size) {
                throw RuntimeException(error)
            } else {
                val item = equipped[index].second
                return ItemInfo(item, true, index)
            }
        } else if (Inventory.unwornSlotNames.contains(slot)) {
            val index = inventoryIndex + Inventory.unwornSlotNames.indexOf(slot)
            val unequipped = unequipped
            if (index >= unequipped.size) {
                throw RuntimeException(error)
            } else {
                val item = unequipped[index]
                return ItemInfo(item, false, index)
            }
        } else {
            throw RuntimeException(error)
        }
    }
}