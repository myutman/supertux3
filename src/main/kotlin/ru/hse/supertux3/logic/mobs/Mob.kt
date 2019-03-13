package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Coordinates

/**
 * Interface for mobs (including player).
 */
interface Mob {
    /**
     * Base health points of mob.
     */
    var HP: Int
    /**
     * Base damage of mob.
     */
    var damage: Int
    /**
     * Base resist chance of mob.
     */
    var resistChance: Int
    /**
     * Base armor of mob.
     */
    var armor: Int
    /**
     * Base mob' chance of critical hit.
     */
    var criticalChance: Int

    /**
     * Mob's position.
     */
    var position: Coordinates
}