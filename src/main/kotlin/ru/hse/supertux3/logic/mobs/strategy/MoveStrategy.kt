package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob

/**
 * Interface that determines how NPC moves on level.
 */
abstract class MoveStrategy(val id: String) {
    /**
     * Function that returns move for npc on level.
     */
    abstract fun move(level: Level, mob: Mob): Move
}

/**
 * Container for move data: direction and how far this move goes.
 */
data class Move(val direction: Direction, val r: Int) {
    /**
     * Returns move in opposite direction to given move and same length.
     */
    fun opposite(): Move {

        val oppositeDirection = when (direction) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.RIGHT -> Direction.LEFT
            Direction.LEFT -> Direction.RIGHT
        }
        return Move(oppositeDirection, r)
    }
}
