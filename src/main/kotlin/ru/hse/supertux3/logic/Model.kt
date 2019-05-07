package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.mobs.NPC
import ru.hse.supertux3.logic.items.Wearable
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.ui.View
import ru.hse.supertux3.ui.readChar

/**
 * Class that changes game state according to given actions and asks view to redraw field.
 */
class Model(val state: GameState) {
    /**
     * State of game, including level and player.
     */
    val level = state.level

    /**
     * View to request to redraw everything.
     */
    lateinit var view: View

    fun putOn() {
        println("What do you want to put on?")
        val c = readChar()
    }

    fun putOff() {
        println("What do you want to put off?")

    }

    fun loot() {
        val floor = state.level.getCell(state.player.position()) as Floor
        if (floor.items.isEmpty()) return
        state.player.inventory.unequipped.addAll(floor.items)
        floor.items.clear()

        view.redraw()
        afterAction()
    }

    /**
     * Move player in given direction (if possible).
     */
    fun move(direction: Direction) {
        val moveData = state.player.processMove(direction, level)

        when (moveData.result) {
            MoveResult.FAILED -> return
            MoveResult.MOVED -> view.move(direction)
            MoveResult.ATTACKED -> {
                state.player.addXp()
                view.attack()
            }
            MoveResult.DIED -> handleDeath()
        }

        afterAction()
    }

    /**
     * Reduces player's health.
     */
    fun selfHarm() {
        val npc = Snowball(Cell(Coordinates(0, 0, 0, 0), ""))
        npc.damage = 20
        npc.attack(state.player, level)

        if (state.player.isDead()) {
            handleDeath()
        } else {
            view.attacked()
        }

        afterAction()
    }

    /**
     * Function that moves player deeper by ladder.
     */
    fun moveLadder() {
        val level = state.level
        val position = state.player.position()

        val cell = level.getCell(position)
        if (cell is Ladder) run {
            cell.stander = null
            val newCell = level.getCell(cell.destination)
            (newCell as Floor).stander = state.player

            state.player.cell = newCell
            view.moveLadder()
        }
    }

    /**
     * Process everything that happens after player's move.
     */
    fun afterAction() {
        level.mobs.forEach { mob ->
            if (!mob.isDead()) {
                val result = mob.move(level)
                if (result.affected is Player) {
                    view.attacked()
                }
            }
        }

        level.mobs.forEach { mob ->
            if (mob.isDead()) {
                level.setCell(mob.position(), mob.cell)
                (mob.cell as Floor).stander = null
                (mob.cell as Floor).drop(mob.drop)
            }
        }

        level.mobs.removeIf { it.isDead() }

        view.afterAction()

        if (state.player.isDead()) {
            handleDeath()
        }
    }

    /**
     * Functions to be done when player was killed
     */
    fun handleDeath() {
        view.died()
    }
}