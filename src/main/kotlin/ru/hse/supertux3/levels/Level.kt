package ru.hse.supertux3.levels

import kotlin.random.Random

data class Coordinates(val i: Int, val j: Int, val h: Int, val level: Level)

enum class Direction {
    UP, DOWN, RIGHT, LEFT
}

class Level(val depth: Int, val height: Int, val width: Int) {
    private val field: Array<Array<Array<Cell>>> = Array(depth) { h ->
        Array(height + 2) { i ->
            Array(width + 2) { j ->
                    if (i == 0 || j == 0 || i == height + 1 || j == width + 1){
                        Wall(Coordinates(i, j, h, this))
                    } else {
                        Floor.empty(Coordinates(i, j, h, this))
                    }
            }
        }

    }

    fun setCell(c: Coordinates, cell: Cell) {
        field[c.h][c.i][c.j] = cell
    }

    fun getCell(c: Coordinates): Cell {
        return field[c.h][c.i][c.j]
    }

    fun getCell(c: Coordinates, direction: Direction, r: Int): Cell {
        val (i,j) = getNewCoordinate(c, direction, r)
        return field[c.h][i][j]
    }

    fun canGo(c: Coordinates, direction: Direction, r: Int): Boolean {
        val (i,j) = getNewCoordinate(c, direction, r)
        return i >= 0 || j >= 0 || i <= height + 1 || j <= width + 1
    }

    private fun getNewCoordinate(c: Coordinates, direction: Direction, r: Int): Pair<Int, Int> {
        return when (direction) {
            Direction.RIGHT -> Pair(c.i + r, c.j)
            Direction.LEFT -> Pair(c.i - r, c.j)
            Direction.UP -> Pair(c.i, c.j - r)
            Direction.DOWN -> Pair(c.i, c.j + r)
        }
    }

    fun randomCell() = getCell(randomCoordinates())

    fun randomCoordinates() = Coordinates(
        Random.nextInt(1, height + 1),
        Random.nextInt(1, width + 1),
        Random.nextInt(0, depth),
        this)

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        for (stage in field) {
            for (row in stage) {
                for (cell in row) {
                    stringBuilder.append(cell)
                }
                stringBuilder.append('\n')
            }
            stringBuilder.append("\n\n\n")
        }
        return stringBuilder.toString()
    }
}