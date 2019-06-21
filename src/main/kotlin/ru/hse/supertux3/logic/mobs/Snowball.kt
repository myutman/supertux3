package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.MoveData
import ru.hse.supertux3.logic.items.Item
import ru.hse.supertux3.logic.mobs.strategy.MoveStrategy
import ru.hse.supertux3.logic.mobs.strategy.NeutralStrategy

/**
 * Basic and weakest enemy in the game.
 */
class Snowball(cell: Cell) : NPC(cell, "ё") {
    override var hp: Int = 50

    override var resistChance: Int = 0

    override var armor: Int = 0

    override var damage: Int = 10

    override var criticalChance: Int = 0

    override var level: Int = 0
        set(value) {
            damage += (field - value) * 5
            hp += (field - value) * 10
            field = value
        }

    override var moveStrategy: MoveStrategy = NeutralStrategy()

    override val drop = mutableListOf<Item>()

    override fun move(level: Level): MoveData {
        val move = moveStrategy.move(level, this)
        return move(move, level)
    }
}