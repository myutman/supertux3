package ru.hse.supertux3.levels

import ru.hse.supertux3.levels.Level
import java.lang.Exception
import java.util.*
import kotlin.random.Random

fun main() {
    print(LevelGenerator.generate(1, 15, 20))
}

class LevelGenerator(private val depth: Int, private val height: Int, val width: Int) {
    private val level = Level(depth, height, width)
    fun generate(): Level {
        if (height * width * depth < 100) {
            throw Exception("To small field size...")
        }
        val roomsCount = Random.nextInt(height * width * depth / 100, height * width * depth / 50)
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
        return level
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
                            if (checkCanExpand(c, direction, r, startCell.roomNumber)) {
                                val nextCell = level.getCell(c, direction, r) as Floor
                                nextCell.roomNumber = startCell.roomNumber
                                room.push(nextCell)
                                fulledCells++
                                if (fulledCells == height * width) {
                                    return
                                }
                            } else {
//                                if (nextCell.roomNumber != startCell.roomNumber) {
//                                    //nextCell.toWall()
//                                }
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
            return if (direction == Direction.UP || direction == Direction.DOWN) {
                checkLeftRight(nextCell.coordinates, Direction.RIGHT, Direction.LEFT, roomNumber)
            } else {
                checkLeftRight(nextCell.coordinates, Direction.UP, Direction.DOWN, roomNumber)
            }
        }

        return false
    }

    private fun checkLeftRight(c: Coordinates, direction1: Direction, direction2: Direction, roomNumber: Int): Boolean {
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