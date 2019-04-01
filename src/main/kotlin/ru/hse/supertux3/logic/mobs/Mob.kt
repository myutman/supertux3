package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Coordinates

/**
 * Interface for mobs (including player). Mobs are things that can:
 * - attack
 * - move
 * - live and die
 */
interface Mob {
    /**
     * Base health points of creature.
     */
    var hp: Int
    /**
     * Base resist chance of mob.
     */
    var resistChance: Int
    /**
     * Base armor of mob.
     */
    var armor: Int
    /**
     * Base damage of mob.
     */
    var damage: Int
    /**
     * Base mob' chance of critical hit.
     */
    var criticalChance: Int

    /**
     * Creature's position.
     */
    var position: Coordinates

    /**
     * Function that processes battle turn
     */
    fun attack(enemy: Mob)
}