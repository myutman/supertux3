package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import org.jline.terminal.Terminal
import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.GameState
import ru.hse.supertux3.logic.items.Inventory
import ru.hse.supertux3.logic.items.Item
import java.lang.RuntimeException
import kotlin.math.max
import kotlin.math.min

class View(val state: GameState, val visual: TermColors, val terminal: Terminal) {

    private var inventoryCur: Int
    private val inventoryWindowSize: Int

    init {
        inventoryCur = 0
        inventoryWindowSize = 10
        redraw()
    }

    fun moveLadder() {
        redraw()
    }

    fun move(direction: Direction) {
        val prevPosition = state.level.getCell(state.player.position(), direction, -1)
        visual.run {
            print(prevPosition)
            print(cursorLeft(1))
        }

        visual.run {
            when (direction) {
                Direction.UP -> print(cursorUp(1))
                Direction.DOWN -> print(cursorDown(1))
                Direction.RIGHT -> print(cursorRight(1))
                Direction.LEFT -> print(cursorLeft(1))
            }
        }

        clearMonstersNotSeen(prevPosition.coordinates)
        clearAttacked()
    }

    fun afterAction() {
        drawBeingSeen()

        visual.run {
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
    }

    fun attack() {
        printUsrInfo()
        printStrInLine("You attacked", 2)
    }

    fun attacked() {
        printUsrInfo()
        printAttacked()
    }

    fun died() {
        clearScreen()
        print(buildString {
            append(
                "You die!", System.lineSeparator(),
                "And words won't do anything", System.lineSeparator(),
                "It's permanently night", System.lineSeparator(),
                "And I won't feel anything", System.lineSeparator(),
                "We'll all be laughing with you when you die", System.lineSeparator(),
                System.lineSeparator(),
                "Press any key to exit..."
            )
        })
        readChar()
    }

    private data class ItemInfo(val item: Item, val isEquipped: Boolean, val index: Int)

    private fun getItemInfoBySlot(slot: Char): ItemInfo {
        val error = "No element in selected slot"
            if (Inventory.wornSlotNames.contains(slot)) {
            val index = Inventory.wornSlotNames.indexOf(slot)
            val equipped = state.player.inventory.equipped.toList()
            if (index >= equipped.size) {
                throw RuntimeException(error)
            } else {
                val item = equipped[index].second
                return ItemInfo(item, true, index)
            }
        } else if (Inventory.unwornSlotNames.contains(slot)) {
            val index = inventoryCur + Inventory.unwornSlotNames.indexOf(slot)
            val unequipped = state.player.inventory.unequipped
            if (index >= unequipped.size) {
                throw RuntimeException(error)
            } else {
                val item = unequipped[index]
                return ItemInfo(item, false, index)
            }
        } else {
            throw RuntimeException(error)
        }
    }

    fun showInfo(slot: Char) {
        redraw()
        val str: String = try {
            val itemInfo = getItemInfoBySlot(slot)
            itemInfo.item.description
        } catch (e: RuntimeException) {
            e.message!!
        } + "\nPress ESC to continue"
        printStrInLine(str, 4)
    }

    private fun drawCell(new: Cell, str: String = "") {
        val cur = state.player.position()
        val coordinates = new.coordinates
        val right = coordinates.j - cur.j
        val down = coordinates.i - cur.i
        visual.run {
            print(cursorRight(right))
            print(cursorDown(down))

            print(if (str.isEmpty()) new.toString() else str)
            print(cursorLeft(1))

            print(cursorLeft(right))
            print(cursorUp(down))
        }
    }

    fun redraw() {
        clearScreen()

        val level = state.level
        val position = state.player.position()

        for (i in 0 until level.height) {
            for (j in 0 until level.width) {
                val cell = level.getCell(i, j, position.h)
                val symb = cell.toString()
                visual.run {
                    if (cell.visibility == Visibility.Visible) {
                        if (symb != "&") {
                            print(rgb("#ffffff")(symb))
                        } else {
                            print(red(symb))
                        }
                    } else {
                        print(' ')
                    }
                }
            }
            visual.run {
                println()
            }
        }

        val up = level.height - position.i
        val right = position.j

        visual.run {
            print(cursorUp(up))
            print(cursorRight(right))
        }

        drawBeingSeen()

        visual.run {
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
        printUsrInfo()
        printInventoryInfo()
    }

    private fun clearMonstersNotSeen(prevPosition: Coordinates) {
        state.level.bfs(prevPosition, state.player.visibilityDepth) {
            if (it is Floor) {
                if (it.stander != null) {
                    drawCell(it, ".")
                }
            }
        }
    }

    private fun drawBeingSeen() {
        val cur = state.player.position()
        state.level.bfs(cur, state.player.visibilityDepth) {
            drawCell(it)
            it.visibility = Visibility.Visible
        }
    }

    private fun printStrInLine(toPrint: String, lineNumber: Int): Int {
        val level = state.level
        val position = state.player.position()
        val offset = 20

        var i = 0
        val toPrintList = toPrint.split(System.lineSeparator())

        val len = terminal.width - level.width - offset

        for (line in toPrintList) {
            var rest = line
            while (rest.length > 0) {
                val myLen = min(len, rest.length)
                val str = rest.substring(0, myLen)
                rest = rest.substring(myLen)
                val down = level.height - position.i + lineNumber + i
                val left = position.j

                visual.run {
                    print(cursorLeft(left))
                    print(cursorDown(down))


                    print(str)
                    print(cursorLeft(str.length))

                    print(cursorUp(down))
                    print(cursorRight(left))
                }

                i++
            }
        }

        return i
    }

    private fun printPos() {
        val level = state.level
        val position = state.player.position()

        val str = buildString {
            append(
                "pos(i,j,h) = ",
                position.i,
                ", ",
                position.j,
                ", ",
                position.h,
                " height = ",
                level.height,
                " width = ",
                level.width,
                "      "
            )
        }

        printStrInLine(str, 1)
    }

    private fun printUsrInfo() {
        val player = state.player

        val str = buildString {
            append(
                "Level:",
                player.level,
                ", XP:",
                player.xp,
                ", HP:",
                player.hp,
                ", Armor:",
                player.armor,
                ", Damage:",
                player.damage,
                ", Cricital chance:",
                player.criticalChance,
                "      "
            )
        }

        printStrInLine(str, 0)
    }

    fun slideDown() {
        if (inventoryCur + inventoryWindowSize + 1 < state.player.inventory.unequipped.size) {
            inventoryCur++
            redraw()
        }
    }

    fun slideUp() {
        if (inventoryCur > 0) {
            inventoryCur--
            redraw()
        }
    }

    fun showInventoryMessage() {
        val str = "What item do you want to know about?"
        printStrInLine(str, 4)
    }

    private fun printInventoryInfo() {
        var line = 0
        line += printStrInLineRight("Inventory", line)
        line += printStrInLineRight("Unequipped:", line)
        val inventory = state.player.inventory
        inventoryCur = max(0, min(inventoryCur, inventory.unequipped.size - Inventory.unwornSlotNames.size))
        for (i in 0..Inventory.unwornSlotNames.size - 1) {
            val slotName = Inventory.unwornSlotNames[i]
            if (inventoryCur + i < inventory.unequipped.size) {
                val item = inventory.unequipped[inventoryCur + i]
                line += printStrInLineRight("[$slotName] " + item.toString(), line)
            } else {
                line += printStrInLineRight("[$slotName] --", line)
            }
        }
        line += printStrInLineRight("To slide up press j. To slide down press k.", line)
        line++
        line += printStrInLineRight("Equipped:", line)
        val worns = inventory.equipped.toList()
        for (i in 0..worns.size - 1) {
            val entry = worns[i]
            val slotName = Inventory.wornSlotNames[i]
            line += printStrInLineRight("[$slotName] ${entry.second} (being worn)", line)
        }
    }

    private fun printStrInLineRight(toPrint: String, lineNumber: Int): Int {
        val level = state.level
        val position = state.player.position()
        val offset = 20

        var i = 0
        val toPrintList = toPrint.split(System.lineSeparator())

        val len = terminal.width - level.width - offset

        for (line in toPrintList) {
            var rest = line
            while (rest.length > 0) {
                val myLen = min(len, rest.length)
                val str = rest.substring(0, myLen)
                rest = rest.substring(myLen)
                val down = lineNumber + i - position.i
                val right = level.width + offset - position.j

                visual.run {
                    print(cursorRight(right))
                    print(cursorDown(down))

                    print(str)
                    print(cursorLeft(str.length))

                    print(cursorUp(down))
                    print(cursorLeft(right))
                }

                i++
            }
        }

        return i
    }

    private fun printAttacked() {
        printStrInLine("You were attacked", 2)
    }

    private fun clearAttacked() {
        printStrInLine("                 ", 2)
    }
}