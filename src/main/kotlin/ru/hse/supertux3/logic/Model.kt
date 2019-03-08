package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.ui.View

class Model(val depth: Int, val heightWithWalls: Int, val widthWithWalls: Int) {
    val state: GameState

    init {
        val level = LevelGenerator.generate(depth, heightWithWalls, widthWithWalls)
        val player = Player(position = level.randomCoordinates())
        state = GameState(level, player)
    }

    lateinit var view: View


    fun check(position: Coordinates): Boolean {
        val level = state.level
        return level.canGo(position, Direction.UP, 0)
                && level.getCell(position) !is Wall
    }



    fun moveUp() {
        val position = state.player.position
        val newPosition = position.copy(i = position.i - 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveUp()
        }
    }

    fun moveDown() {
        val position = state.player.position
        val newPosition = position.copy(i = position.i + 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveDown()
        }
    }

    fun moveLeft() {
        val position = state.player.position
        val newPosition = position.copy(j = position.j - 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveLeft()
        }
    }

    fun moveRight() {
        val position = state.player.position
        val newPosition = position.copy(j = position.j + 1)
        if (check(newPosition)) {
            state.player.position = newPosition
            view.moveRight()
        }
    }

    fun moveLadder() {
        val level = state.level
        val position = state.player.position

        val cell = level.getCell(position)
        if (cell is Ladder) run {
            state.player.position = position.copy(h = cell.destination)
            view.moveLadder()
        }
    }
}