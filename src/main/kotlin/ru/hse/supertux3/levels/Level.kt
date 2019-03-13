package ru.hse.supertux3.levels

import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.File
import kotlin.random.Random

data class Coordinates(val i: Int, val j: Int, val h: Int, val levelId: Int) {
    fun serialize() = "$i $j $h $levelId"
}

enum class Direction {
    UP, DOWN, RIGHT, LEFT
}

class Level(val depth: Int, val height: Int, val width: Int, id: Int = -1) {

    val id = if (id == -1) Level.maxId++ else id

    val field: Array<Array<Array<Cell>>> = Array(depth) { h ->
        Array(height) { i ->
            Array(width) { j ->
                    if (i == 0 || j == 0 || i == height - 1 || j == width - 1){
                        Wall(Coordinates(i, j, h, id))
                    } else {
                        Floor.empty(Coordinates(i, j, h, id))
                    }
            }
        }

    }


    fun buildWall(c: Coordinates) {
        val newWall = Wall(c)
        setCell(c, newWall)
    }

    fun setCell(c: Coordinates, cell: Cell) {
        field[c.h][c.i][c.j] = cell
    }

    fun setCell(i: Int, j: Int, h: Int, cell: Cell) {
        field[h][i][j] = cell
    }

    fun getCell(c: Coordinates): Cell {
        return field[c.h][c.i][c.j]
    }

    fun getCell(i: Int, j: Int, h: Int): Cell {
        return field[h][i][j]
    }

    fun getCell(c: Coordinates, direction: Direction, r: Int): Cell {
        val (i,j) = getNewCoordinate(c, direction, r)
        return field[c.h][i][j]
    }

    fun canGo(c: Coordinates, direction: Direction, r: Int): Boolean {
        val (i,j) = getNewCoordinate(c, direction, r)
        return i >= 0 || j >= 0 || i < height || j < width
    }

    private fun getNewCoordinate(c: Coordinates, direction: Direction, r: Int): Pair<Int, Int> {
        return when (direction) {
            Direction.LEFT -> Pair(c.i, c.j - r)
            Direction.RIGHT -> Pair(c.i, c.j + r)
            Direction.DOWN -> Pair(c.i + r, c.j)
            Direction.UP -> Pair(c.i - r, c.j)
        }
    }

    fun randomCell() = getCell(randomCoordinates())

    fun randomCoordinates() = Coordinates(
        Random.nextInt(1, height - 1),
        Random.nextInt(1, width - 1),
        Random.nextInt(0, depth),
        id)

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



    fun save(fileName: String) {
        File(fileName).writeText(json.toJsonString(this))
    }

    companion object {
        var maxId = 1
        val json = Klaxon()

        fun load(fileName: String): Level {
            val jsonLevel =  json.parseJsonObject(File(fileName).reader())!!
            val height = jsonLevel.int("height")!!
            val width = jsonLevel.int("width")!!
            val depth = jsonLevel.int("depth")!!
            val levelId = jsonLevel.int("id")!!
            val level = Level(depth, height, width, levelId)
            val field = jsonLevel.array<JsonArray<JsonArray<JsonObject>>>("field")!!
            for (h in 0 until depth) {
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        val cellJson = field.get(h).get(i).get(j)
                        val id = cellJson.string("id")
                        val c = Coordinates(i, j, h, levelId)
                        when (id) {
                            "." -> level.setCell(c, Floor.empty(c))
                            "&" -> level.setCell(c, Floor.chest(c))
                            "#" -> level.setCell(c, Wall(c))
                            "L" -> {
                                val destJson = cellJson.obj("destination")!!
                                val destination = Coordinates(
                                    destJson.int("i")!!,
                                    destJson.int("j")!!,
                                    destJson.int("h")!!,
                                    levelId)
                                level.setCell(c, Ladder(c, destination))
                            }
                        }
                    }
                }
            }

            return level
        }
    }
}