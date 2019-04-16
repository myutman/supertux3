package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.MoveResult
import ru.hse.supertux3.logic.mobs.strategy.MoveStrategy

/**
 * Interface for non-playable characters. They must move by themselves.
 */
abstract class NPC(cell: Cell, id: String) : Mob(cell, id) {
    /**
     * Level of this mob, which should determine its stats
     */
    abstract var level: Int

    /**
     * Strategy used by this NPC to calculate moves.
     */
    abstract var moveStrategy: MoveStrategy

    /**
     * Function to move this NPC.
     */
    abstract fun move(level: Level): MoveResult
}