package ru.hse.supertux3.levels

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import java.io.File
import kotlin.random.Random

/**
 * Data class to store coordinates in level
 */
data class Coordinates(val i: Int, val j: Int, val h: Int, val levelId: Int)

/**
 * Directions where you can go from cell
 */
enum class Direction {
    UP, DOWN, RIGHT, LEFT
}

/**
 * This is one level in our game - depth of stages, which are just matrixes of cells
 */
class Level(val depth: Int, val height: Int, val width: Int, id: Int = -1) {

    /**
     * Unique level id
     */
    val id = if (id == -1) Level.maxId++ else id

    /**
     * Just a 3D array representation of field,
     * it has to be public, otherwise Json doesn't work
     */
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

    /**
     * Set new wall in coordinates
     */
    fun buildWall(c: Coordinates) {
        val newWall = Wall(c)
        setCell(c, newWall)
    }
    /**
     * Set cell in coordinates
     */
    fun setCell(c: Coordinates, cell: Cell) {
        field[c.h][c.i][c.j] = cell
    }

    /**
     * Set cell in i j h coordinates
     */
    fun setCell(i: Int, j: Int, h: Int, cell: Cell) {
        field[h][i][j] = cell
    }

    /**
     * Get cell in coordinates
     */
    fun getCell(c: Coordinates): Cell {
        return field[c.h][c.i][c.j]
    }
    /**
     * Get cell in i j h coordinates
     */
    fun getCell(i: Int, j: Int, h: Int): Cell {
        return field[h][i][j]
    }

    /**
     * Get cell in coordinates, moved in some direction by some distance from start coordinate
     */
    fun getCell(c: Coordinates, direction: Direction, r: Int): Cell {
        val (i,j) = getNewCoordinate(c, direction, r)
        return field[c.h][i][j]
    }

    /**
     * Can you move to the coordinates, moved in some direction by some distance from start coordinate
     */
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

    /**
     * Get random cell in any stage(not floor!!!)
     */
    fun randomCell() = getCell(randomCoordinates())

    /**
     * Get random coordinates
     */
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


    /**
     * Saves level to file
     */
    fun save(fileName: String) {
        File(fileName).writeText(json.toJsonString(this))
    }

    companion object {
        private var maxId = 1
        private val json = Klaxon()

        /**
         * Returns level, loaded from file
         */
        fun load(fileName: String): Level {
            val jsonLevel =  json.parseJsonObject(File(fileName).reader())
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
                            "O" -> level.setCell(c, Door(c))
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