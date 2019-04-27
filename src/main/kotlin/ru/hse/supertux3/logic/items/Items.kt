package ru.hse.supertux3.logic.items

import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Player
import kotlin.math.max
import kotlin.math.min

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
abstract class Wearable(description: String, name: String, val type: WearableType): Item(description, name) {

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

class WearableBuilder(private val description: String, private val name: String, private val type: WearableType) {

    var resistChance: Int = 0

    var armor: Int = 0

    var damage: Int = 0

    var criticalChance: Int = 0
    
    fun build(): Wearable = object : Wearable(description, name, type) {
        override fun putOn(player: Player) {
            player.resistChance = min(100, player.resistChance + resistChance)
            player.armor += armor
            player.damage += damage
            player.criticalChance = min(100, player.criticalChance + criticalChance)
        }

        override fun takeOff(player: Player) {
            player.resistChance = max(0, player.resistChance + resistChance)
            player.armor -= armor
            player.damage = max(0, player.damage -damage)
            player.criticalChance = max(0, player.criticalChance - criticalChance)
        }

    }
}