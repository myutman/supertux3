package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.Coordinates
import ru.hse.supertux3.logic.mobs.Mob


class MoveData(val initiator: Mob, val destination: Coordinates) {
    var result: MoveResult = MoveResult.FAILED
    var affected: Mob? = null
}
