package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.GameState
import ru.hse.supertux3.logic.mobs.Mob
import ru.hse.supertux3.logic.mobs.NPC
import kotlin.math.max

/*
 * Class for moving cursor commands.
 * @param state current game state consists of level and player information
 * @param visual object for moving cursor and coloring symbols
 */
class View(val state: GameState, val visual: TermColors) {

    init {
        redraw()
    }

    /*
     * Move up or down the ladder.
     */
    fun moveLadder() {
        redraw()
    }
    /*
     * Go to the given direction.
     * @param direction direction to go to
     */
    fun move(direction: Direction) {
        val prevPosition = state.level.getCell(state.player.position, direction, -1)
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

    private fun drawCell(new: Cell, str: String = "") {
        val cur = state.player.position()
        val coordinates = new.coordinates
        val left = max(cur.j - coordinates.j, 0)
        val right = max(coordinates.j - cur.j, 0)
        val up = max(cur.i - coordinates.i, 0)
        val down = max(coordinates.i - cur.i, 0)
        visual.run {
            print(cursorLeft(left))
            print(cursorRight(right))
            print(cursorUp(up))
            print(cursorDown(down))

            print(if (str.isEmpty()) new.toString() else str)
            print(cursorLeft(1))

            print(cursorLeft(right))
            print(cursorRight(left))
            print(cursorUp(down))
            print(cursorDown(up))
        }
    }

    /*
     * Redraw all the field.
     */
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

    private fun printStrInLine(str: String, lineNumber: Int) {
        val level = state.level
        val position = state.player.position()

        val up = level.height - position.i + lineNumber
        val right = position.j

        visual.run {
            print(cursorLeft(right))
            print(cursorDown(up))


            print(str)
            print(cursorLeft(str.length))

            print(cursorUp(up))
            print(cursorRight(right))
        }
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
                "XP:",
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

    private fun printAttacked() {
        printStrInLine("You were attacked", 2)
    }

    private fun clearAttacked() {
        printStrInLine("                 ", 2)
    }
}