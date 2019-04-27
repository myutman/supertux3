package ru.hse.supertux3.logic.items

class Inventory {
    val equipped = mutableMapOf<WearableType, Wearable>()
    val unequipped = mutableListOf<Wearable>()
}