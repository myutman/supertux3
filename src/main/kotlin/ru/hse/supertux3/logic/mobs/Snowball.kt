package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.MoveResult
import ru.hse.supertux3.logic.mobs.strategy.MoveStrategy
import ru.hse.supertux3.logic.mobs.strategy.NeutralStrategy

class Snowball(cell: Cell) : NPC(cell, "ё") {
    override var hp: Int = 1

    override var resistChance: Int = 0

    override var armor: Int = 0

    override var damage: Int = 1

    override var criticalChance: Int = 0

    override var level: Int = 1

    override var moveStrategy: MoveStrategy = NeutralStrategy()

    override fun move(level: Level): MoveResult {
        val move = moveStrategy.move(level, this)
        return move(move, level)
    }
}