package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.CellStander
import ru.hse.supertux3.levels.Coordinates
import ru.hse.supertux3.logic.MoveResult

/**
 * Interface for mobs (including player). Mob is basically any creature in game.
 * Mobs are divided in two groups:
 * - player
 * - non-playable characters.
 */
abstract class Mob(cell: Cell, id: String) : CellStander(cell, id) {
    /**
     * Base health points of creature.
     */
    abstract var hp: Int
    /**
     * Base resist chance of mob.
     */
    abstract var resistChance: Int
    /**
     * Base armor of mob.
     */
    abstract var armor: Int
    /**
     * Base damage of mob.
     */
    abstract var damage: Int
    /**
     * Base mob' chance of critical hit.
     */
    abstract var criticalChance: Int

    /**
     * Mob's position.
     */
    fun position(): Coordinates = cell.coordinates

    /**
     * Function to attack NPC.
     */
    fun attack(mob: Mob): MoveResult {
        val n1 = (0..100).random()
        val n2 = (0..100).random()

        var baseDamage = damage
        if (criticalChance > n1) {
            baseDamage *= 2
        }

        if (mob.resistChance > n2) {
            baseDamage /= 2
        }

        baseDamage -= mob.armor

        mob.hp -= baseDamage

        return MoveResult.ATTACKED
    }
}