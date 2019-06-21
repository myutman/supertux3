package ru.hse.supertux3.logic.items

import ru.hse.supertux3.InventoryOuterClass
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Player

/**
 * Basic item class
 * @param description full description of item
 * @param name short name
 */
abstract class Item(val description: String, val name: String, val id: String) {

    /**
     * Item may have any effect on the game, so it can do anything with level
     */
    abstract fun interact(level: Level)

    override fun toString(): String {
        return name
    }

    open fun toProto(): InventoryOuterClass.Item {
        return InventoryOuterClass.Item.newBuilder()
            .setDescription(description)
            .setId(id)
            .setName(name)
            .build()
    }
}

/**
 * Items, that upgrades player's characteristics
 */
abstract class Wearable(
    description: String, name: String,
    val type: WearableType, id: String
) : Item(description, name, id) {

    var resistChance: Int = 0

    var armor: Int = 0

    var damage: Int = 0

    var criticalChance: Int = 0

    /**
     * Puts this on the player
     */
    fun putOn(player: Player) {
        player.resistChance = player.resistChance + resistChance
        player.armor += armor
        player.damage += damage
        player.criticalChance = player.criticalChance + criticalChance
        doPutOn(player)
    }

    protected abstract fun doPutOn(player: Player)

    /**
     * Take this off the player
     */
    fun takeOff(player: Player) {
        player.resistChance = player.resistChance - resistChance
        player.armor -= armor
        player.damage = player.damage - damage
        player.criticalChance = player.criticalChance - criticalChance
    }

    protected abstract fun doTakeOff(player: Player)

    override fun interact(level: Level) {
        // pass
    }

    override fun toString(): String {
        return super.toString() + ", $type"
    }

    override fun toProto(): InventoryOuterClass.Item {
        val item = super.toProto()
        val wearable = InventoryOuterClass.Wearable.newBuilder()
            .setArmor(armor)
            .setCriticalChance(criticalChance)
            .setDamage(damage)
            .setResistChance(resistChance)
            .setType(type.toString())
            .build()
        return item.toBuilder().setWearable(wearable).build()
    }
}

/**
 * Type of wearable, can be only one in inventory
 */
enum class WearableType { HAT, JACKET, GLOVES, PANTS, SHOES, WEAPON }

/**
 * Classic builder for Wearable item
 */
class WearableBuilder(private val description: String, private val name: String, private val type: WearableType) {

    var resistChance: Int = 0

    var armor: Int = 0

    var damage: Int = 0

    var criticalChance: Int = 0

    /**
     * Creates new wearable from current stats
     */
    fun build(): Wearable {
        val item = object : Wearable(description, name, type, "B") {
            override fun doPutOn(player: Player) {

            }

            override fun doTakeOff(player: Player) {

            }
        }

        item.resistChance = resistChance
        item.damage = damage
        item.armor = armor
        item.criticalChance = criticalChance
        return item
    }
}