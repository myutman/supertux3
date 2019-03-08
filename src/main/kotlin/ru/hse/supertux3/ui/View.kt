package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import ru.hse.supertux3.levels.*

class View(val level: Level, val visual: TermColors) {
    private var position: Coordinates = level.randomCell().coordinates

    init {
        redraw()
    }

    fun redraw() {
        print("\u001Bc")

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

    fun check(position: Coordinates): Boolean {
        return level.canGo(position, Direction.UP, 0)
                && level.getCell(position) !is Wall
    }

    fun moveUp() {
        if (check(position.copy(i = position.i - 1))) {
            val symb = level.getCell(position).toString()
            position = position.copy(i = position.i - 1)

            visual.run {
                print(symb)
                print(cursorLeft(1))
                print(cursorUp(1))
                print(red("@"))
                print(cursorLeft(1))
            }
        }
        printPos()
    }

    fun moveDown() {
        if (check(position.copy(i = position.i + 1))) {
            val symb = level.getCell(position).toString()
            position = position.copy(i = position.i + 1)

            visual.run {
                print(symb)
                print(cursorLeft(1))
                print(cursorDown(1))
                print(red("@"))
                print(cursorLeft(1))
            }
        }
        printPos()
    }

    fun moveLeft() {
        if (check(position.copy(j = position.j - 1))) {
            val symb = level.getCell(position).toString()
            position = position.copy(j = position.j - 1)


            visual.run {
                print(symb)
                print(cursorLeft(1))
                print(cursorLeft(1))
                print(red("@"))
                print(cursorLeft(1))
            }

        }
        printPos()
    }

    fun moveRight() {
        if (check(position.copy(j = position.j + 1))) {
            val symb = level.getCell(position).toString()
            position = position.copy(j = position.j + 1)


            visual.run {
                print(symb)
                print(cursorLeft(1))
                print(cursorRight(1))
                print(red("@"))
                print(cursorLeft(1))
            }
        }
        printPos()
    }

    fun moveLadder() {
        val cell = level.getCell(position)
        if (cell is Ladder) run {
            position = position.copy(h = cell.destination)
            redraw()
        }
    }

    fun printPos() {

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