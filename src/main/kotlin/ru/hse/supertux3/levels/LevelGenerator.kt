package ru.hse.supertux3.levels

import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.logic.mobs.strategy.AggressiveStrategy
import ru.hse.supertux3.logic.mobs.strategy.CowardStrategy
import ru.hse.supertux3.logic.mobs.strategy.NeutralStrategy
import java.util.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.random.Random
import kotlin.random.nextInt

/**
 * Class for generating new levels with given depth, width and high
 * width * high should be >= 100 cause I need it to create rooms correctly
 * @param depth should be >= 1
 * @param heightWithWalls should be >= 4 and even
 * @param widthWithWalls should be >= 4 and even
 */
class LevelGenerator(private val depth: Int, private val heightWithWalls: Int, private val widthWithWalls: Int) {
    private val height = heightWithWalls - 2
    private val width = widthWithWalls - 2
    private val halfHeight = height / 2
    private val halfWidth = width / 2
    private val roomsCount = max(
        Random.nextInt(
            sqrt(halfHeight * halfWidth * depth.toDouble()).toInt(),
            2 * sqrt(halfHeight * halfWidth * depth.toDouble()).toInt()
        ), depth
    )
    private val graph = Array(roomsCount) {
        mutableListOf<Pair<Int, Cell>>()
    }
    private val bfsQueue = LinkedList<Int>()
    private val used = Array(roomsCount) { false }

    companion object {
        /**
         * This static method is the only way you can generate levels
         */
        fun generate(depth: Int, height: Int, width: Int) = LevelGenerator(depth, height, width).generate()

        private val MAX_ROOM_EXPAND = 3
    }

    private fun generate(): Level {
        if (heightWithWalls * widthWithWalls < 100) {
            throw LevelGeneratorException("height * width is < 100")
        }
        if (heightWithWalls < 4) {
            throw LevelGeneratorException("height is too small")
        }
        if (heightWithWalls % 2 != 0) {
            throw LevelGeneratorException("height is odd")
        }
        if (widthWithWalls < 4) {
            throw LevelGeneratorException("width is too small")
        }
        if (widthWithWalls % 2 != 0) {
            throw LevelGeneratorException("width is odd")
        }
        if (depth < 1) {
            throw LevelGeneratorException("depth < 1")
        }
        val smallLevel = createRooms()
        val level = buildWalls(smallLevel)
        if (depth != 1) {
            makeLadders(level)
        }
        bfs(level)
        addItems(level)
        addMobs(level)
        return level
    }

    private fun createRooms(): Level {
        var nextRoomNumber = 0
        val roomCells = Array<LinkedList<Floor>>(roomsCount) {
            LinkedList()
        }
        val level = Level(depth, halfHeight + 2, halfWidth + 2)
        for (i in 0 until roomsCount) {
            while (true) {
                val randomCell = if (i >= depth) {
                    level.randomCell()
                } else {
                    level.getCell(
                        Random.nextInt(1, halfHeight), Random.nextInt(1, halfWidth), i
                    )
                }
                if (randomCell is Floor) {
                    if (randomCell.roomNumber >= 0) {
                        continue
                    } else {
                        randomCell.roomNumber = nextRoomNumber
                        nextRoomNumber++
                        roomCells[i].add(randomCell)
                        break
                    }
                }
            }
        }
        expandRooms(roomCells, level)
        return level
    }

    private fun expandRoom(room: LinkedList<Floor>, level: Level): Int {
        var fulledCells = 0
        val startCell = room.pop()
        val c = startCell.coordinates
        for (direction in Direction.values()) {
            for (r in 1..Random.nextInt(1, MAX_ROOM_EXPAND + 1)) {
                val nextCell = level.getCell(c, direction, r)
                if (nextCell is Floor) {
                    if (checkCanExpand(c, direction, r, level)) {
                        nextCell.roomNumber = startCell.roomNumber
                        room.push(nextCell)
                        fulledCells++
                    } else {
                        break
                    }
                } else {
                    break
                }
            }
        }
        return fulledCells
    }

    private fun expandRooms(roomCells: Array<LinkedList<Floor>>, level: Level) {
        var fulledCells = roomCells.size
        while (fulledCells < halfHeight * halfWidth * depth) {
            for (room in roomCells) {
                if (!room.isEmpty()) {
                    fulledCells += expandRoom(room, level)
                    if (fulledCells >= halfHeight * halfWidth * depth) {
                        return
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
        val bigLevel = Level(depth, heightWithWalls, widthWithWalls)
        for (h in 0 until depth) {
            for (i in 0 until halfHeight) {
                for (j in 0 until halfWidth) {
                    val cell = level.getCell(i + 1, j + 1, h) as Floor
                    val roomNumber = cell.roomNumber
                    val right = level.getCell(i + 1, j + 2, h)
                    val down = level.getCell(i + 2, j + 1, h)
                    //     cell
                    // [-----------]
                    // cell00 cell01  right right
                    // cell10 cell11  right right
                    //
                    //  down   down
                    //  down   down
                    //
                    // We have cell in small level, and it maps to 4 cells in big level
                    // (cause it width and height are 2 times bigger)
                    //
                    val cell00 = bigLevel.getCell(1 + 2 * i, 1 + 2 * j, h) as Floor
                    val cell01 = bigLevel.getCell(1 + 2 * i, 1 + 2 * j + 1, h) as Floor
                    val cell10 = bigLevel.getCell(1 + 2 * i + 1, 1 + 2 * j, h) as Floor
                    val cell11 = bigLevel.getCell(1 + 2 * i + 1, 1 + 2 * j + 1, h) as Floor
                    cell00.roomNumber = roomNumber
                    cell01.roomNumber = roomNumber
                    cell10.roomNumber = roomNumber
                    cell11.roomNumber = roomNumber

                    // We need to build walls if right is another room or down is another room
                    if (right is Floor && right.roomNumber != roomNumber) {
                        bigLevel.buildWall(cell01.coordinates)
                        bigLevel.buildWall(cell11.coordinates)
                        val doors = possibleDoors[min(roomNumber, right.roomNumber)]
                            .getOrPut(max(roomNumber, right.roomNumber)) { mutableListOf() }
                        doors.add(cell01)
                    }
                    if (down is Floor && down.roomNumber != roomNumber) {
                        bigLevel.buildWall(cell10.coordinates)
                        bigLevel.buildWall(cell11.coordinates)
                        val doors = possibleDoors[min(roomNumber, down.roomNumber)]
                            .getOrPut(max(roomNumber, down.roomNumber)) { mutableListOf() }
                        doors.add(cell10)
                    }
                }
            }
        }
        for (u in possibleDoors.indices) {
            for ((v, doors) in possibleDoors[u]) {
                val chosenDoor = doors.random()
                graph[u].add(Pair(v, chosenDoor))
                graph[v].add(Pair(u, chosenDoor))
            }
        }
        return bigLevel
    }

    private fun makeLadders(level: Level) {
        val ladders = mutableSetOf<Pair<Int, Int>>()
        val laddersCount = sqrt(roomsCount.toDouble()).toInt() * (depth - 1)
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
                        val from = c
                        val to = Coordinates(c.i, c.j, c.h - 1, level.id)
                        level.setCell(from, Ladder(from, to))
                        level.setCell(to, Ladder(to, from))
                        val ladder = level.getCell(c)
                        graph[v].add(Pair(u, ladder))
                        graph[u].add(Pair(v, ladder))
                        break
                    }
                }
            }
        }
    }

    private fun bfs(level: Level) {
        bfsQueue.add(0)
        while (!bfsQueue.isEmpty()) {
            val v = bfsQueue.pollFirst()
            dfsLadders(v)
            for ((u, cell) in graph[v]) {
                if (!used[u]) {
                    // In normal bfs they set used[u] = true here
                    // but without it we re generating nice random cycles
                    level.setCell(cell.coordinates, Door(cell.coordinates))
                    bfsQueue.add(u)
                }
            }
        }
    }

    private fun dfsLadders(v: Int) {
        used[v] = true
        for ((u, cell) in graph[v]) {
            if (!used[u] && cell is Ladder) {
                bfsQueue.push(u)
                dfsLadders(u)
            }
        }
    }

    private fun addItems(level: Level) {
        val c = level.randomFloor().coordinates
        level.setCell(c, Floor.chest(c))
    }

    private fun addMobs(level: Level) {
        val mobsCount = Random.nextInt(roomsCount, 2 * roomsCount)
        for (i in 1..mobsCount) {
            val mob = Snowball(level.randomCell())
            val rand = Random.nextInt(1..3)
            val strategy = when (rand) {
                1 -> AggressiveStrategy()
                2 -> NeutralStrategy()
                3 -> CowardStrategy()
                else -> AggressiveStrategy()
            }
            mob.level = level.player?.level ?: 0
            mob.moveStrategy = strategy
            val itemsCount = Random.nextInt(1, 4)
            mob.drop.addAll(ItemsGenerator.generateItems(itemsCount, level.player?.level ?: 0))
            level.putMob(mob)
        }
    }
}