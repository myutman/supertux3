package ru.hse.supertux3.logic.items

import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Player

/**
 * Basic item class
 * @param description full description of item
 * @param name short name
 */
abstract class Item(val description: String, val name: String) {
    /**
     * Item may have any effect on the game, so it can do anything with level
     */
    abstract fun interact(level: Level)
}

abstract class Wearable(description: String, name: String): Item(description, name) {

    abstract fun putOn(player: Player)
    abstract fun takeOff(player: Player)

    override fun interact(level: Level) {
        // pass
    }
}

enum class WearableType { HAT, JACKET, GLOVES, PANTS, SHOES, WEAPON }