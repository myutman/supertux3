package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.items.Wearable
import ru.hse.supertux3.logic.items.WearableType
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.ui.View
import ru.hse.supertux3.ui.readChar
import java.lang.RuntimeException

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

    private fun message(str: String) {
        view.printMessage("$str${System.lineSeparator()}Press ESC to continue")
        while (true) {
            if (readChar().toInt() == 27) break
        }
        view.redraw()
    }

    fun putOn(index: Int) {
        val equipped = state.player.inventory.equipped
        val unequipped = state.player.inventory.unequipped

        val item: Wearable = unequipped[index] as Wearable
        item.putOn(state.player)
        unequipped.removeAt(index)
        equipped.put(item.type, item)
        view.redraw()

        afterAction()
    }

    fun putOff(type: WearableType) {
        val equipped = state.player.inventory.equipped
        val unequipped = state.player.inventory.unequipped
        val item = equipped[type]!!
        item.takeOff(state.player)
        equipped.remove(type)
        unequipped.add(state.player.inventory.inventoryCur, item)
        view.redraw()
        afterAction()
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
                (mob.cell as Floor).items.addAll(mob.drop)
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

    fun getSlotToPutOn(slotChar: Char): Int {
        val equipped = state.player.inventory.equipped
        val unequipped = state.player.inventory.unequipped
        val info = try {
             state.player.inventory.getItemInfoBySlot(slotChar)
        } catch (e: RuntimeException) {
            message(e.message!!)
            return -1
        }
        if (info.isEquipped) {
            message("Item is already equipped")
            return -1
        }
        val item = unequipped[info.index]
        if (item !is Wearable) {
            message("Item is not wearable")
            return -1
        }
        if (equipped.containsKey(item.type)) {
            message("${item.type} is already equipped")
            return -1
        }
        return info.index
    }

    fun getSlotToTakeOff(slotChar: Char): WearableType? {
        val equipped = state.player.inventory.equipped
        val entry = try {
            val info = state.player.inventory.getItemInfoBySlot(slotChar)
            if (!info.isEquipped) {
                message("Item is not equipped")
                return null
            }
            equipped.toList()[info.index]
        } catch (e: RuntimeException) {
            message(e.message!!)
            return null
        }
        return entry.first
    }
}