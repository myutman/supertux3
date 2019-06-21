package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.Coordinates
import ru.hse.supertux3.logic.mobs.Mob

/**
 * Move representation
 */
class MoveData(val initiator: Mob, val destination: Coordinates) {
    /**
     * Move result
     */
    var result: MoveResult = MoveResult.FAILED
    /**
     * Affected mob
     */
    var affected: Mob? = null
}
