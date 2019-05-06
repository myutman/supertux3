package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob

/**
 * Strategy that makes npc do nothing.
 */
class NeutralStrategy : MoveStrategy {
    override fun move(level: Level, mob: Mob): Move {
        // Going 0 cells to the right - just staying
        return Move(Direction.RIGHT, 0)
    }
}