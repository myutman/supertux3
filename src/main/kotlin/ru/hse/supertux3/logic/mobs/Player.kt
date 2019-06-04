package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.LevelOuterClass
import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.MoveData
import ru.hse.supertux3.logic.items.Inventory
import ru.hse.supertux3.logic.mobs.strategy.Move

/**
 * Class that has all player data, such as characteristics and inventory.
 */
class Player(
    cell: Cell,
    val userId: Int = 0,
    override var hp: Int = 100,
    override var damage: Int = 15,
    override var resistChance: Int = 10,
    override var armor: Int = 5,
    override var criticalChance: Int = 10,
    val inventory: Inventory = Inventory()
) : Mob(cell, "@") {
    var xp: Int = 0
    var level: Int = 0

    init {
        if (cell is Floor) {
            cell.stander = this
        }
    }

    /**
     * Processes move of player.
     */
    fun processMove(direction: Direction, level: Level): MoveData {
        val move = Move(direction, 1)
        return move(move, level)
    }

    /**
     * Processes player XP addition.
     */
    fun addXp() {
        xp += 5
        if (xp >= (level + 1) * 10) {
            xp = 0
            levelUp()
        }
    }

    fun levelUp() {
        level += 1
        hp = 100 + level * 25
        damage += 3
        resistChance += 5
        armor += 1
        criticalChance += 5
    }


    override fun toProto(): LevelOuterClass.Mob {
        val mob = super.toProto()
        val player = LevelOuterClass.Player.newBuilder()
            .setInventory(inventory.toProto())
            .setLevel(level)
            .setXp(xp)
            .setUserId(userId)
            .build()
        return mob.toBuilder().setPlayer(player).build()
    }

    fun copyFrom(stander: Player) {
        hp = stander.hp
        damage = stander.damage
        resistChance = stander.resistChance
        armor = stander.armor
        criticalChance = stander.criticalChance
        xp = stander.xp
        level = stander.level
        cell = stander.cell
    }
}