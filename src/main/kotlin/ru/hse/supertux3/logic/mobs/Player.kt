package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.MoveResult
import ru.hse.supertux3.logic.mobs.strategy.Move

/**
 * Class that has all player data, such as characteristics and inventory.
 */
class Player(
    cell: Cell,
    override var hp: Int = 100,
    override var damage: Int = 10,
    override var resistChance: Int = 50,
    override var armor: Int = 5,
    override var criticalChance: Int = 50
    ) : Mob(cell, "@") {
    var xp: Int = 0

    init {
        if (cell is Floor) {
            cell.stander = this
        }
    }

    /**
     * Processes move of player.
     */
    fun processMove(direction: Direction, level: Level): MoveResult {
        val move = Move(direction, 1)
        return move(move, level)
    }

    // some inventory fields will be added in future
}