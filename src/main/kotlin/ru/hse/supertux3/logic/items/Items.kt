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

/**
 * Items, that upgrades player's characteristics
 */
abstract class Wearable(description: String, name: String): Item(description, name) {

    /**
     * Puts this on the player
     */
    abstract fun putOn(player: Player)

    /**
     * Take this off the player
     */
    abstract fun takeOff(player: Player)

    override fun interact(level: Level) {
        // pass
    }
}

/**
 * Type of wearable, can be only one in inventory
 */
enum class WearableType { HAT, JACKET, GLOVES, PANTS, SHOES, WEAPON }