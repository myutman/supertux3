package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.items.Wearable
import ru.hse.supertux3.logic.items.WearableType
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.ui.ViewLike

/**
 * Class that changes game state according to given actions and asks view to redraw field.
 */
open class Model(val state: GameState, val view: ViewLike) {
    /**
     * State of game, including level and player.
     */
    val level = state.level

    /**
     * Puts on selected unworn item.
     * @param index index of unworn item to put on
     */
    open fun putOn(index: Int) {
        val equipped = state.player.inventory.equipped
        val unequipped = state.player.inventory.unequipped

        view.clearInventoryInfo()

        val item: Wearable = unequipped[index] as Wearable
        item.putOn(state.player)
        unequipped.removeAt(index)
        equipped.put(item.type, item)

        view.printInventoryInfo()
    }

    /**
     * Takes off selected worn item.
     * @param type type of worn item to take off
     */
    open fun takeOff(type: WearableType) {
        val equipped = state.player.inventory.equipped
        val unequipped = state.player.inventory.unequipped

        view.clearInventoryInfo()

        val item = equipped[type]!!
        item.takeOff(state.player)
        equipped.remove(type)
        unequipped.add(state.player.inventory.inventoryCur, item)

        view.printInventoryInfo()
    }

    /**
     * Loots all the items in current cell.
     */
    open fun loot() {
        val floor = state.level.getCell(state.player.position()) as Floor
        if (floor.items.isEmpty()) return

        view.clearInventoryInfo()

        state.player.inventory.unequipped.addAll(floor.items)
        floor.items.clear()

        view.printInventoryInfo()
    }

    /**
     * Move player in given direction (if possible).
     */
    open fun move(direction: Direction) {
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
    }

    /**
     * Reduces player's health.
     */
    open fun selfHarm() {
        val npc = Snowball(Cell(Coordinates(0, 0, 0, 0), ""))
        npc.damage = 20
        npc.attack(state.player, level)

        if (state.player.isDead()) {
            handleDeath()
        } else {
            view.attacked()
        }
    }

    /**
     * Function that moves player deeper by ladder.
     */
    open fun moveLadder() {
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
    fun afterAction(): List<Coordinates> {
        val changed = ArrayList<Coordinates>()
        level.mobs.forEach { mob ->
            if (!mob.isDead()) {
                val old = mob.cell.coordinates.copy()
                val result = mob.move(level)
                val new = mob.cell.coordinates.copy()
                if (result.affected is Player) {
                    view.attacked()
                }
                changed.add(old)
                changed.add(new)
            }
        }

        level.mobs.forEach { mob ->
            if (mob.isDead()) {
                level.setCell(mob.position(), mob.cell)
                (mob.cell as Floor).stander = null
                (mob.cell as Floor).items.addAll(mob.drop)
            }
        }

        level.mobs.removeIf { it.isDead() }

        view.afterAction()

        if (state.player.isDead()) {
            handleDeath()
        }
        return changed
    }

    /**
     * Functions to be done when player was killed
     */
    fun handleDeath() {
        level.players.remove(state.player)
        view.died()
    }
}