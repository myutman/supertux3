package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob

class AggressiveStrategy : MoveStrategy {
    private val coward = CowardStrategy()
    override fun move(level: Level, mob: Mob): Move {
        val opposite = coward.move(level, mob)
        val direction = when (opposite.direction) {
            Direction.UP -> Direction.DOWN
            Direction.DOWN -> Direction.UP
            Direction.RIGHT -> Direction.LEFT
            Direction.LEFT -> Direction.RIGHT
        }
        return Move(direction, opposite.r)
    }
}