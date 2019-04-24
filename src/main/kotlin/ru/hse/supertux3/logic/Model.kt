package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.mobs.NPC
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
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
        val position = level.randomFloor()
        val player = Player(level.getCell(position.coordinates))
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

        afterAction(level)
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
    fun afterAction(level: Level) {
        level.mobs.forEach { mob ->
            if (mob is Player)
                return

            if (!mob.isDead()) {
                (mob as NPC).move(level)
            }
        }

        level.mobs.forEach { mob ->
            if (mob is Player)
                return

            if (mob.isDead()) {
                level.setCell(mob.position(), mob.cell)
                (mob.cell as Floor).stander = null
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