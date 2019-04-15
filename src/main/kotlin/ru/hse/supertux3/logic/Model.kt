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
        val position = level.randomCoordinates()
        val player = Player(level.getCell(position))
        state = GameState(level, player)
    }

    /**
     * View to request to redraw everything.
     */
    lateinit var view: View


    /**
     * Move player in given direction (if possible).
     */
    fun move(direction: Direction) {
        val moveResult = state.player.processMove(direction, level)

        when (moveResult) {
            MoveResult.FAILED -> return
            MoveResult.MOVED -> view.move(direction)
            MoveResult.ATTACKED -> {} // TODO
            MoveResult.DIED -> {} // TODO
        }

        afterAction(level)
    }

    /**
     * Function that moves player deeper by ladder.
     */
    fun moveLadder() {
        val level = state.level
        val position = state.player.position()

        val cell = level.getCell(position)
        if (cell is Ladder) run {
            state.player.cell = level.getCell(cell.destination)
            view.moveLadder()
        }
    }

    /**
     * Process everything that happens after player's move.
     */
    fun afterAction(level: Level) {
        // TODO: update npc, update effects, check if player isn't dead
    }
}