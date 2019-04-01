package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.strategy.MoveStrategy

/**
 * Interface for non-playable characters. They are mobs, but also they must move by themselves.
 */
interface NPC : Mob {
    /**
     * Strategy used by this NPC to calculate moves.
     */
    var moveStrategy: MoveStrategy

    fun move(level: Level)
}