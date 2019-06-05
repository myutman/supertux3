package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.GameState
import ru.hse.supertux3.logic.items.Inventory
import org.jline.terminal.Terminal
import kotlin.math.min

interface ViewLike {
    fun moveLadder()
    fun move(direction: Direction)
    fun afterAction()
    fun attack()
    fun attacked()
    fun died()
    fun printMessage(str: String)
    fun redraw()
    fun lazyRedraw(cells: List<Cell>)
    fun slideDown()
    fun slideUp()
    fun showInventoryMessage()
}

class FakeView: ViewLike {
    override fun moveLadder() {

    }

    override fun move(direction: Direction) {

    }

    override fun afterAction() {

    }

    override fun attack() {

    }

    override fun attacked() {

    }

    override fun died() {

    }

    override fun printMessage(str: String) {

    }

    override fun redraw() {

    }

    override fun lazyRedraw(cells: List<Cell>) {

    }

    override fun slideDown() {

    }

    override fun slideUp() {

    }

    override fun showInventoryMessage() {

    }
}


/*
 * Class for moving cursor commands.
 * @param state current game state consists of level and player information
 * @param visual object for moving cursor and coloring symbols
 */
class View(val state: GameState, val visual: TermColors, val terminal: Terminal): ViewLike {

    init {
        redraw()
    }

    val inventoryView = InventoryView(this)

    /*
     * Move up or down the ladder.
     */
    override fun moveLadder() {
        redraw()
    }

    /*
     * Go to the given direction.
     * @param direction direction to go to
     */
    override fun move(direction: Direction) {
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

    override fun afterAction() {
        drawBeingSeen()

        visual.run {
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
    }

    override fun attack() {
        printUsrInfo()
        printStrInLine("You attacked", 2)
    }

    override fun attacked() {
        printUsrInfo()
        printAttacked()
    }

    override fun died() {
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

    override fun printMessage(str: String) {
        printStrInLine(str, 5)
    }

    private fun drawCell(new: Cell, str: String = "") {
        if (new.visibility == Visibility.Hidden) {
            return
        }
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

    /*
     * Redraw all the field.
     */
    override fun redraw() {
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

        printStrInLine("Press h to open help", 3)
        printPos()
        printUsrInfo()
        printInventoryInfo()
    }

    // TODO: перенести туда, где это должно быть
    var prevPosition = state.player.position()
    // TODO: help!
    override fun lazyRedraw(cells: List<Cell>) {
        val position = state.player.position()

        clearMonstersNotSeen(prevPosition)
        prevPosition = position

        drawBeingSeen()

        printPos()

        for (cell in cells) {
            if (cell.visibility == Visibility.Visible) {
                if (cell.coordinates == state.player.coordinates) {
                    visual.run {
                        drawCell(cell, red("@"))
                        print(cursorLeft(1))
                    }
                } else {
                    drawCell(cell)
                }
            }
        }
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
            it.visibility = Visibility.Visible
            drawCell(it)
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
            if (line.isEmpty()) {
                i++
                continue
            }
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

    override fun slideDown() {
        if (state.player.inventory.slideDown())
            redraw()
    }

    override fun slideUp() {
        if (state.player.inventory.slideUp())
            redraw()
    }

    override fun showInventoryMessage() {
        val str = "What item do you want to know about?"
        printStrInLine(str, 4)
    }

    private fun printInventoryInfo() {
        var line = 0
        line += printStrInLineRight("Inventory", line)
        line += printStrInLineRight("Unequipped:", line)
        val inventory = state.player.inventory
        for (i in 0..Inventory.unwornSlotNames.size - 1) {
            val slotName = Inventory.unwornSlotNames[i]
            if (state.player.inventory.inventoryCur + i < inventory.unequipped.size) {
                val item = inventory.unequipped[state.player.inventory.inventoryCur + i]
                line += printStrInLineRight("[$slotName] $item", line)
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
            if (line.isEmpty()) {
                i++
                continue
            }
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