package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import ru.hse.supertux3.logic.GameState

class View(val state: GameState, val visual: TermColors) {

    init {
        redraw()
    }

    fun redraw() {
        print("\u001Bc")

        val level = state.level
        val position = state.player.position

        for (i in 0 until level.height) {
            for (j in 0 until level.width) {
                val symb = level.getCell(i, j, position.h).toString()
                visual.run {
                    print(rgb("#ffffff")(symb))
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
    }

    fun moveUp() {
        val level = state.level
        val position = state.player.position

        val symb = level.getCell(position).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorUp(1))
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
    }

    fun moveDown() {
        val level = state.level
        val position = state.player.position

        val symb = level.getCell(position).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorDown(1))
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
    }

    fun moveLeft() {
        val level = state.level
        val position = state.player.position

        val symb = level.getCell(position).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorLeft(1))
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
    }

    fun moveRight() {
        val level = state.level
        val position = state.player.position

        val symb = level.getCell(position).toString()

        visual.run {
            print(symb)
            print(cursorLeft(1))
            print(cursorRight(1))
            print(red("@"))
            print(cursorLeft(1))
        }

        printPos()
    }

    fun moveLadder() {
        redraw()
    }

    fun printPos() {
        val level = state.level
        val position = state.player.position

        val up = level.height - position.i
        val right = position.j

        val str = buildString {
            append("pos(i,j,h) = ", position.i, ", ", position.j, ", ", position.h, " height = ", level.height, " width = ", level.width, "      ")
        }

        visual.run {
            print(cursorLeft(right))
            print(cursorDown(up))


            print(str)
            print(cursorLeft(str.length))

            print(cursorUp(up))
            print(cursorRight(right))
        }
    }
}