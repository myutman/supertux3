package ru.hse.supertux3.levels

import ru.hse.supertux3.levels.Level
import java.lang.Exception
import java.util.*
import kotlin.random.Random

fun main() {
    print(LevelGenerator.generate(1, 30, 40))
}

class LevelGenerator(private val depth: Int, private var height: Int, var width: Int) {
    private var level = Level(depth, height, width)
    fun generate(): Level {
        height /= 2
        width /= 2
        if (height * width * depth < 100) {
            throw Exception("To small field size...")
        }
        val roomsCount = Random.nextInt(height * width * depth / 50, height * width * depth / 30)
        val roomCells = Array<LinkedList<Floor>>(roomsCount) {
            LinkedList()
        }
        for (i in 0 until roomsCount) {
            while (true) {
                val randomCell = level.randomCell()
                if (randomCell is Wall) {
                    continue
                } else if (randomCell is Floor) {
                    if (randomCell.roomNumber == 0) {
                        continue
                    } else {
                        randomCell.newRoom()
                        roomCells[i].add(randomCell)
                        break
                    }
                }
            }
        }
        expandRooms(roomCells)
        buildWalls()
        height *= 2
        width *= 2
        return level
    }

    private fun buildWalls() {
        val bigLevel = Level(depth, height * 2, width * 2)
        for (h in 0 until depth) {
            for (i in 0 until height) {
                for (j in 0 until width) {
                    val cell = level.getCell(i + 1, j + 1, h) as Floor
                    val right = level.getCell(i + 1, j + 2, h)
                    val down = level.getCell(i + 2, j + 1, h)
                    val cell1 = bigLevel.getCell(1 + 2 * i, 1 + 2 * j, h) as Floor
                    val cell2 = bigLevel.getCell(1 + 2 * i, 1 + 2 * j + 1, h) as Floor
                    val cell3 = bigLevel.getCell(1 + 2 * i + 1, 1 + 2 * j, h) as Floor
                    val cell4 = bigLevel.getCell(1 + 2 * i + 1, 1 + 2 * j + 1, h) as Floor
                    cell1.roomNumber = cell.roomNumber
                    cell2.roomNumber = cell.roomNumber
                    cell3.roomNumber = cell.roomNumber
                    cell4.roomNumber = cell.roomNumber
                    if (right is Floor && right.roomNumber != cell.roomNumber) {
                        cell2.toWall()
                        cell4.toWall()
                    }
                    if (down is Floor && down.roomNumber != cell.roomNumber) {
                        cell3.toWall()
                        cell4.toWall()
                    }
                }
            }
        }
        print(level)
        level = bigLevel
    }

    private fun expandRooms(roomCells: Array<LinkedList<Floor>>) {
        var fulledCells = roomCells.size
        val maxExpand = height * width * depth / 100
        while (fulledCells < height * width) {
            for (room in roomCells) {
                if (!room.isEmpty()) {
                    val startCell = room.pop()
                    val c = startCell.coordinates
                    for (direction in Direction.values()) {
                        for (r in 1..Random.nextInt(1, maxExpand + 1)) {
                            val nextCell = level.getCell(c, direction, r)
                            if (nextCell is Floor) {
                                if (checkCanExpand(c, direction, r, startCell.roomNumber)) {
                                    nextCell.roomNumber = startCell.roomNumber
                                    room.push(nextCell)
                                    fulledCells++
                                    if (fulledCells == height * width) {
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

    private fun checkCanExpand(c: Coordinates, direction: Direction, r: Int, roomNumber: Int): Boolean {
        val nextCell = level.getCell(c, direction, r)
        if (!level.canGo(c, direction, r)) {
            return false
        }
        if (nextCell is Floor && nextCell.roomNumber == -1) {
            return true
        }

        return false
    }

    private fun checkLeftRight(
        c: Coordinates, direction1: Direction, direction2: Direction,
        roomNumber: Int
    ): Boolean {
        val right = level.getCell(c, direction1, 1)
        val left = level.getCell(c, direction2, 1)
        val isOurRoomRight = right is Floor && (right.roomNumber == roomNumber || right.roomNumber == -1)
        val isOurRoomLeft = left is Floor && (left.roomNumber == roomNumber || left.roomNumber == -1)
        return isOurRoomRight || isOurRoomLeft
    }

    companion object {
        fun generate(depth: Int, height: Int, width: Int) = LevelGenerator(depth, height, width).generate()
    }
}