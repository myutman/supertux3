package ru.hse.supertux3.levels

import ru.hse.supertux3.levels.Level
import java.lang.Exception
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

fun main() {
//    for (i in 0..1000)
    print(LevelGenerator.generate(4, 30, 40))
}

// height and width should be even
class LevelGenerator(val depth: Int, val heightWithWalls: Int, val widthWithWalls: Int) {
    private val height = heightWithWalls
    private val width = widthWithWalls
    private val halfHeight = height / 2
    private val halfWidth = width / 2
    private val roomsCount = Random.nextInt(
        halfHeight * halfWidth * depth / 40,
        halfHeight * halfWidth * depth / 20
    )
    private val graph = Array(roomsCount) {
        mutableListOf<Pair<Int, Cell>>()
    }
    private val used = Array(roomsCount) { false }

    fun generate(): Level {
        Floor.nextRoomNumber = 0
        if (height * width * depth < 100) {
            throw Exception("To small field size...")
        }
        val smallLevel = createRooms()
        val level = buildWalls(smallLevel)
        return level
    }

    private fun createRooms(): Level {
        val roomCells = Array<LinkedList<Floor>>(roomsCount) {
            LinkedList()
        }
        val level = Level(depth, halfHeight, halfWidth)
        for (i in 0 until roomsCount) {
            while (true) {
                val randomCell = level.randomCell()
                if (randomCell is Wall) {
                    continue
                } else if (randomCell is Floor) {
                    if (randomCell.roomNumber >= 0) {
                        continue
                    } else {
                        randomCell.newRoom()
                        roomCells[i].add(randomCell)
                        break
                    }
                }
            }
        }
        expandRooms(roomCells, level)
        return level
    }

    private fun expandRooms(roomCells: Array<LinkedList<Floor>>, level: Level) {
        var fulledCells = roomCells.size
        val maxExpand = 3
        while (fulledCells < halfHeight * halfWidth * depth) {
            for (room in roomCells) {
                if (!room.isEmpty()) {
                    val startCell = room.pop()
                    val c = startCell.coordinates
                    for (direction in Direction.values()) {
                        for (r in 1..Random.nextInt(1, maxExpand + 1)) {
                            val nextCell = level.getCell(c, direction, r)
                            if (nextCell is Floor) {
                                if (checkCanExpand(c, direction, r, level)) {
                                    nextCell.roomNumber = startCell.roomNumber
                                    if (nextCell.roomNumber == -1 || startCell.roomNumber == -1) {
                                        throw Exception("FUCK")
                                    }
                                    room.push(nextCell)
                                    fulledCells++
                                    if (fulledCells >= halfHeight * halfWidth * depth) {
                                        return
                                    }
                                } else {
                                    break
                                }
                            } else {
                                break
                            }
                        }
                    }
                }
            }
        }
    }

    private fun checkCanExpand(c: Coordinates, direction: Direction, r: Int, level: Level): Boolean {
        if (!level.canGo(c, direction, r)) {
            return false
        }
        val nextCell = level.getCell(c, direction, r)
        if (nextCell is Wall) {
            return false
        }
        if (nextCell is Floor && nextCell.roomNumber == -1) {
            return true
        }

        return false
    }

    private fun buildWalls(level: Level): Level {
        val possibleDoors = Array(roomsCount) {
            mutableMapOf<Int, MutableList<Cell>>()
        }
        val bigLevel = Level(depth, height, width)
        for (h in 0 until depth) {
            for (i in 0 until halfHeight) {
                for (j in 0 until halfWidth) {
                    val cell = level.getCell(i + 1, j + 1, h) as Floor
                    val roomNumber = cell.roomNumber
                    val right = level.getCell(i + 1, j + 2, h)
                    val down = level.getCell(i + 2, j + 1, h)
                    val cell1 = bigLevel.getCell(1 + 2 * i, 1 + 2 * j, h) as Floor
                    val cell2 = bigLevel.getCell(1 + 2 * i, 1 + 2 * j + 1, h) as Floor
                    val cell3 = bigLevel.getCell(1 + 2 * i + 1, 1 + 2 * j, h) as Floor
                    val cell4 = bigLevel.getCell(1 + 2 * i + 1, 1 + 2 * j + 1, h) as Floor
                    cell1.roomNumber = roomNumber
                    cell2.roomNumber = roomNumber
                    cell3.roomNumber = roomNumber
                    cell4.roomNumber = roomNumber
                    if (right is Floor && right.roomNumber != roomNumber) {
                        bigLevel.buildWall(cell2.coordinates)
                        bigLevel.buildWall(cell4.coordinates)
                        val doors = possibleDoors[min(roomNumber, right.roomNumber)]
                            .getOrPut(max(roomNumber, right.roomNumber)) { mutableListOf() }
                        doors.add(cell2)
                    }
                    if (down is Floor && down.roomNumber != roomNumber) {
                        bigLevel.buildWall(cell3.coordinates)
                        bigLevel.buildWall(cell4.coordinates)
                        val doors = possibleDoors[min(roomNumber, down.roomNumber)]
                            .getOrPut(max(roomNumber, down.roomNumber)) { mutableListOf() }
                        doors.add(cell3)
                    }
                }
            }
        }
        for (u in possibleDoors.indices) {
            for ((v, doors) in possibleDoors[u]) {
                val choosenDoor = doors.random()
                graph[u].add(Pair(v, choosenDoor))
                graph[v].add(Pair(u, choosenDoor))
            }
        }
        if (depth != 1) {
            makeLadders(bigLevel)
        }
        dfs(0, bigLevel)
        return bigLevel
    }

    private fun makeLadders(level: Level) {
        val ladders = mutableSetOf<Pair<Int, Int>>()
        val laddersCount = roomsCount / 10 * depth
        for (i in 1..laddersCount) {
            while (true) {
                val randomCell = level.randomCell()
                if (randomCell.coordinates.h == 0 || randomCell is Wall || randomCell is Ladder) {
                    continue
                } else if (randomCell is Floor) {
                    val c = randomCell.coordinates
                    val upCell = level.getCell(c.i, c.j, c.h - 1)
                    if (upCell is Wall || upCell is Ladder) {
                        continue
                    } else if (upCell is Floor) {
                        val v = upCell.roomNumber
                        val u = randomCell.roomNumber
                        if (ladders.contains(Pair(v, u))) {
                            continue
                        }
                        ladders.add(Pair(v, u))
                        ladders.add(Pair(u, v))
                        level.setCell(c.i, c.j, c.h, Ladder(c, c.h - 1))
                        level.setCell(c.i, c.j, c.h - 1, Ladder(
                            Coordinates(c.i, c.j, c.h - 1, level),
                            c.h))
                        val ladder = level.getCell(c)
                        graph[v].add(Pair(u, ladder))
                        graph[u].add(Pair(v, ladder))
                        break
                    }
                }
            }
        }
    }

    private fun dfs(v: Int, level: Level) {
        used[v] = true
        for ((u, cell) in graph[v]) {
            if (used[u]) {
                continue
            }
            if (cell !is Ladder) {
                level.setCell(cell.coordinates, Door(cell.coordinates))
            }
            dfs(u, level)
        }
    }


    companion object {
        fun generate(depth: Int, height: Int, width: Int) = LevelGenerator(depth, height, width).generate()
    }
}