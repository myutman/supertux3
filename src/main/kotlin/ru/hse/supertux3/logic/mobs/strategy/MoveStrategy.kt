package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob

/**
 * Interface that determines how NPC moves on level.
 */
interface MoveStrategy {
    /**
     * Function that returns move for mob on level.
     */
    fun move(level: Level, mob: Mob): Move
}

/**
 * Container for move data: direction and how far this move goes.
 */
data class Move(val direction: Direction, val r: Int)