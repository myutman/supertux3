package ru.hse.supertux3.logic.items

import ru.hse.supertux3.InventoryOuterClass
import java.lang.RuntimeException

/**
 * Player's inventory
 */
class Inventory {

    /**
     * Items that are equipped in hero and have positive or negative effect on him
     */
    val equipped = mutableMapOf<WearableType, Wearable>()

    /**
     * Items that are not equipped in hero
     */
    val unequipped = mutableListOf<Item>()

    private var inventoryIndex = 0

    /**
     * Current inventory index
     */
    val inventoryCur: Int
        get() {
            return inventoryIndex
        }

    companion object {
        /**
         * Unequipped slots names for player choosing in view
         */
        val unwornSlotNames: List<Char> = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')


        /**
         * Equipped slots names for player choosing in view
         */
        val wornSlotNames: List<Char> = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    }

    /**
     * Information of item if it is equipped and where
     */
    data class ItemInfo(val item: Item, val isEquipped: Boolean, val index: Int)

    /**
     * Virtually slides inventory list down
     */
    fun slideDown(): Boolean {
        if (inventoryIndex + unwornSlotNames.size < unequipped.size) {
            inventoryIndex++
            return true
        }
        return false
    }

    /**
     * Virtually slides inventory list up
     */
    fun slideUp(): Boolean {
        if (inventoryIndex > 0) {
            inventoryIndex--
            return true
        }
        return false
    }

    /**
     * Return items that user wanted to
     */
    fun getItemInfoBySlot(slot: Char): ItemInfo {
        val error = "No element in selected slot"
        if (Inventory.wornSlotNames.contains(slot)) {
            val index = Inventory.wornSlotNames.indexOf(slot)
            val equipped = equipped.toList()
            if (index >= equipped.size) {
                throw RuntimeException(error) // TODO
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

    fun toProto(): InventoryOuterClass.Inventory {
        val equippedList = equipped.toList().map { it.second.toProto() }
        val uneqquippedList = unequipped.map { it.toProto() }
        return InventoryOuterClass.Inventory.newBuilder()
            .addAllEquipped(equippedList)
            .addAllUnequipped(uneqquippedList)
            .build()
    }
}