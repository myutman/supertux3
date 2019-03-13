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


    /**
     * Function that moves player up.
     */
    fun moveUp() {
        val position = state.player.position
        val newPosition = position.copy(i = position.i - 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveUp()
        }
    }

    /**
     * Function that moves player down.
     */
    fun moveDown() {
        val position = state.player.position
        val newPosition = position.copy(i = position.i + 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveDown()
        }
    }

    /**
     * Function that moves player left.
     */
    fun moveLeft() {
        val position = state.player.position
        val newPosition = position.copy(j = position.j - 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveLeft()
        }
    }

    /**
     * Function that moves player right.
     */
    fun moveRight() {
        val position = state.player.position
        val newPosition = position.copy(j = position.j + 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveRight()
        }
    }

    /**
     * Function that moves player deeper by ladder.
     */
    fun moveLadder() {
        val level = state.level
        val position = state.player.position

        val cell = level.getCell(position)
        if (cell is Ladder) {
            state.player.position = position.copy(h = cell.destination)
            view.moveLadder()
        }
    }
}