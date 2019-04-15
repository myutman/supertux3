package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.strategy.MoveStrategy

/**
 * Interface for non-playable characters. They are mobs, but also they must move by themselves.
 * Also they can fight for cell, if someone stands there.
 */
abstract class NPC(cell: Cell, id: String) : Mob(cell, id) {
    /**
     * Level of this mob, which should determine
     */
    abstract var level: Int

    /**
     * Strategy used by this NPC to calculate moves.
     */
    abstract var moveStrategy: MoveStrategy

    /**
     * Function to move this NPC.
     */
    abstract fun move(level: Level)
}