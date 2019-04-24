package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.Coordinates
import ru.hse.supertux3.logic.mobs.Mob

/**
 * Enum for results of npc's (mostly player's) move.
 */
enum class MoveResult {
    FAILED,
    MOVED,
    ATTACKED,
    DIED
}

class MoveData(val initiator: Mob, val destination: Coordinates) {
    var result: MoveResult = MoveResult.FAILED
    var affected: Mob? = null
}
