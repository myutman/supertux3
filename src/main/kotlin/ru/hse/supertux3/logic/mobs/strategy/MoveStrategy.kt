package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob


interface MoveStrategy {
    fun move(level: Level, mob: Mob): Move
}

data class Move(val direction: Direction, val r: Int)