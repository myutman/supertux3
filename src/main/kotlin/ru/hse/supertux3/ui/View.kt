package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.logic.GameState

class View(val state: GameState, val visual: TermColors) {

    init {
        redraw()
    }

    fun moveLadder() {
        redraw()
    }

    fun move(direction: Direction) {
        when (direction) {
            Direction.UP -> moveUp()
            Direction.DOWN -> moveDown()
            Direction.RIGHT -> moveRight()
            Direction.LEFT -> moveLeft()
        }

        printPos()
    }

    fun attacked() {
        printUsrInfo()
        printStrInLine("You were attacked", 2)
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

    fun redraw() {
        clearScreen()

        val level = state.level
        val position = state.player.position()

        for (i in 0 until level.height) {
            for (j in 0 until level.width) {
                val symb = level.getCell(i, j, position.h).toString()
                visual.run {
                    if (symb != "&") {
                        print(rgb("#ffffff")(symb))
                    } else {
                        print(red(symb))
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
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
        printUsrInfo()
    }

    private fun moveUp() {
        val level = state.level
        val position = state.player.position()
        val prevPosition = position.copy(i = position.i + 1)

        val symb = level.getCell(prevPosition).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorUp(1))
            print(red("@"))
            print(cursorLeft(1))
        }
    }

    private fun moveDown() {
        val level = state.level
        val position = state.player.position()
        val prevPosition = position.copy(i = position.i - 1)

        val symb = level.getCell(prevPosition).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorDown(1))
            print(red("@"))
            print(cursorLeft(1))
        }
    }

    private fun moveLeft() {
        val level = state.level
        val position = state.player.position()
        val prevPosition = position.copy(j = position.j + 1)

        val symb = level.getCell(prevPosition).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorLeft(1))
            print(red("@"))
            print(cursorLeft(1))
        }
    }

    private fun moveRight() {
        val level = state.level
        val position = state.player.position()
        val prevPosition = position.copy(j = position.j - 1)

        val symb = level.getCell(prevPosition).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorRight(1))
            print(red("@"))
            print(cursorLeft(1))
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
}