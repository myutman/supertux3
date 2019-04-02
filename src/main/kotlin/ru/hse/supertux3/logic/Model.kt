package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.ui.View

/**
 * Class that changes game state according to given actions and asks view to redraw field.
 */
class Model(private val level: Level) {
    /**
     * State of game, including level and player.
     */
    val state: GameState

    init {
        val player = Player(position = level.randomCoordinates())
        state = GameState(level, player)
    }

    /**
     * View to request to redraw everything.
     */
    lateinit var view: View

    private fun check(position: Coordinates): Boolean {
        val level = state.level
        return level.canGo(position, Direction.UP, 0)
                && level.getCell(position) !is Wall
    }

    private val directionToMove: Map<Direction, (Coordinates) -> Coordinates> = mapOf(
        Direction.UP to {position -> position.copy(i = position.i - 1)},
        Direction.DOWN to {position -> position.copy(i = position.i + 1)},
        Direction.LEFT to {position -> position.copy(j = position.j - 1)},
        Direction.RIGHT to {position -> position.copy(j = position.j + 1)}
    )

    /**
     * Move player in given direction (if possible).
     */
    fun move(direction: Direction) {
        val position = state.player.position
        val newPositionFunction = directionToMove.getOrDefault(direction) { position }
        val newPosition = newPositionFunction(position)

        if (check(newPosition)) {
            // TODO: check if attack is necessary
            state.player.position = newPosition
            when (direction) {
                Direction.UP -> view.moveUp()
                Direction.DOWN -> view.moveDown()
                Direction.RIGHT -> view.moveRight()
                Direction.LEFT -> view.moveLeft()
            }
        }
    }

    /**
     * Function that moves player deeper by ladder.
     */
    fun moveLadder() {
        val level = state.level
        val position = state.player.position

        val cell = level.getCell(position)
        if (cell is Ladder) run {
            state.player.position = position.copy(h = cell.destination.h)
            view.moveLadder()
        }
    }

    /**
     * Process everything that happens after player's move.
     */
    fun afterAction() {
        // TODO: update npc, update effects, check if player isn't dead
    }
}