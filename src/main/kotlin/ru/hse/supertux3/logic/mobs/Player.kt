package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.MoveResult

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

    private val directionToMove: Map<Direction, (Coordinates) -> Coordinates> = mapOf(
        Direction.UP to {position -> position.copy(i = position.i - 1)},
        Direction.DOWN to {position -> position.copy(i = position.i + 1)},
        Direction.LEFT to {position -> position.copy(j = position.j - 1)},
        Direction.RIGHT to {position -> position.copy(j = position.j + 1)}
    )


    private fun check(position: Coordinates, level: Level): Boolean {
        return level.canGo(position, Direction.UP, 0)
                && level.getCell(position) !is Wall
    }

    fun processMove(direction: Direction, level: Level): MoveResult {
        val newPositionFunction = directionToMove.getOrDefault(direction) { position() }
        val newPosition = newPositionFunction(position())

        if (!check(newPosition, level)) {
            return MoveResult.FAILED
        }

        val newCell = level.getCell(newPosition)
        if (newCell is Floor && newCell.stander != null) {
            return attack(newCell.stander as Mob)
        }

        cell = newCell
        return MoveResult.MOVED
    }

    // some inventory fields will be added in future
}