package ru.hse.supertux3.levels

import com.beust.klaxon.Json
import com.beust.klaxon.JsonArray
import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import ru.hse.supertux3.logic.mobs.Mob
import java.io.File
import java.util.*
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
class Level(val depth: Int, val height: Int, val width: Int, val id: Int = Level.maxId++) {
  
    /**
     * Mobs who are standing in this level
     */
    val mobs = mutableListOf<Mob>()

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

    fun randomFloor(): Floor {
        var maybeFloor = randomCell()
        while (maybeFloor !is Floor) {
            maybeFloor = randomCell()
        }
        return maybeFloor
    }

    fun putMob(mob: Mob) {
        var floor = randomFloor()
        while (floor.stander != null) {
            floor = randomFloor()
        }
        putMob(mob, floor.coordinates)
        mobs.add(mob)
    }

    fun putMob(mob: Mob, c: Coordinates): Boolean {
        val maybeFloor = getCell(c)
        if (maybeFloor is Floor && maybeFloor.stander == null) {
            maybeFloor.stander = mob
            mob.cell = maybeFloor
            return true
        } else {
            return false
        }
    }

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
                        val cellJson = field[h][i][j]
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


    fun bfs(start: Coordinates, maxDepth: Int, runLogic: (Cell) -> Unit) {
        val used = Array(height) {
            Array(width) {
                0
            }
        }
        used[start.i][start.j] = 1
        val queue = LinkedList<Cell>()
        queue.add(getCell(start))
        while (queue.isNotEmpty()) {
            val curCell = queue.pollFirst()
            val curDepth = used[curCell.coordinates.i][curCell.coordinates.j]
            runLogic(curCell) // It can be a wall, but only one wall near floor
            if (curCell !is Floor || curCell is Door) {
                continue
            } else {
                if (curDepth <= maxDepth) {
                    for (direction in Direction.values()) {
                        if (canGo(curCell.coordinates, direction, 1)) {
                            val next = getCell(curCell.coordinates, direction, 1)
                            if (used[next.coordinates.i][next.coordinates.j] == 0) {
                                used[next.coordinates.i][next.coordinates.j] = curDepth + 1
                                queue.add(next)
                            }
                        }
                    }
                }
            }
        }
    }
}