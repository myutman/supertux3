package ru.hse.supertux3.logic.items

class Inventory {
    val equipped = mutableMapOf<WearableType, Wearable>()
    val unequipped = mutableListOf<Item>()

    companion object {
        val unwornSlotNames: List<Char> = listOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val wornSlotNames: List<Char> = listOf('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h')
    }
}