package ru.hse.supertux3.levels

import ru.hse.supertux3.InventoryOuterClass
import ru.hse.supertux3.LevelOuterClass
import ru.hse.supertux3.logic.items.*
import ru.hse.supertux3.logic.mobs.Mob
import ru.hse.supertux3.logic.mobs.NPC
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.logic.mobs.decorators.MobDecorator
import ru.hse.supertux3.logic.mobs.strategy.AggressiveStrategy
import ru.hse.supertux3.logic.mobs.strategy.CowardStrategy
import ru.hse.supertux3.logic.mobs.strategy.NeutralStrategy
import java.io.File
import java.util.*
import kotlin.random.Random
import kotlin.streams.toList

/**
 * Data class to store coordinates in level
 */
data class Coordinates(val i: Int, val j: Int, val h: Int, val levelId: Int) {
    fun toProto(): LevelOuterClass.Coordinates {
        return LevelOuterClass.Coordinates.newBuilder()
            .setI(i)
            .setJ(j)
            .setH(h)
            .build()
    }

    companion object {
        fun fromProto(levelId: Int, c: LevelOuterClass.Coordinates): Coordinates {
            return Coordinates(c.i, c.j, c.h, levelId)
        }
    }
}

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
    val mobs = mutableListOf<NPC>()

    /**
     * Player, who stands in this level
     */
    var players = mutableListOf<Player>()

    /**
     * The one and only player in singleplayer
     */
    val player: Player?
        get() = if (players.isEmpty()) null else players.first()

    /**
     * Just a 3D array representation of field,
     * it has to be public, otherwise Json doesn't work
     */
    val field: Array<Array<Array<Cell>>> = Array(depth) { h ->
        Array(height) { i ->
            Array(width) { j ->
                if (i == 0 || j == 0 || i == height - 1 || j == width - 1) {
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
        val (i, j) = getNewCoordinate(c, direction, r)
        return field[c.h][i][j]
    }

    /**
     * Can you move to the coordinates, moved in some direction by some distance from start coordinate
     */
    fun canGo(c: Coordinates, direction: Direction, r: Int): Boolean {
        val (i, j) = getNewCoordinate(c, direction, r)
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
        id
    )

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
        if (mob is NPC) {
            mobs.add(mob)
        }
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

    fun createPlayer(userId: Int = 0): Player {
        val cell = randomFloor()
        val player = Player(cell, userId = userId)
        players.add(player)
        return player
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
        File(fileName).writeBytes(toProto().toByteArray())
    }

    /**
     * Converts level, players and mobs to proto format
     */
    fun toProto(): LevelOuterClass.Level {
        val levelBuilder = LevelOuterClass.Level.newBuilder()
            .setId(id)
            .setWidth(width)
            .setHeight(height)
            .setDepth(depth)
        val cells = Arrays.stream(field)
            .flatMap(Arrays::stream)
            .flatMap(Arrays::stream)
            .toList()
            .map { it.toProto() }
        return levelBuilder.addAllCells(cells).build()
    }

    companion object {
        private var maxId = 1

        /**
         * Loads level from file
         */
        fun load(fileName: String): Level {
            return Level.load(LevelOuterClass.Level.parseFrom(File(fileName).inputStream()))
        }

        /**
         * Returns level, loaded from proto format
         */
        fun load(levelProto: LevelOuterClass.Level): Level {
            val level = Level(levelProto.depth, levelProto.height, levelProto.width, levelProto.id)

            for (cellProto in levelProto.cellsList) {
                val id = cellProto.id
                val c = Coordinates.fromProto(levelProto.id, cellProto.coordinates)
                val cell = when (id) {
                    "." -> Floor.empty(c)
                    "&" -> Floor.chest(c)
                    "#" -> Wall(c)
                    "O" -> Door(c)
                    "L" -> {
                        val ladderProto = cellProto.ladder
                        val destination = Coordinates.fromProto(
                            ladderProto.levelId,
                            ladderProto.destinationCoordinates
                        )
                        Ladder(c, destination)
                    }
                    else -> Wall(c)
                }
                level.setCell(c, cell)
                if (cell is Floor && cellProto.hasStander()) {
                    cell.stander = processStander(level, cell, cellProto.stander)
                    val mob = cell.stander
                    if (mob is NPC) {
                        level.mobs.add(mob)
                    } else if (mob is Player){
                        level.players.add(mob)
                    }
                }
            }

            return level
        }

        private fun processStander(level: Level, cell: Cell, stander: LevelOuterClass.Mob): Mob {
            val standerId = stander.id
            val mob: Mob = when (standerId) {
                "ё" -> Snowball(cell)
                "@" -> Player(cell, inventory = processInventory(stander.player.inventory))
                else -> Snowball(cell)
            }

            getMobCharacteristics(mob, stander)

            if (mob is NPC) {
                val npc = stander.npc
                mob.level = npc.level
                mob.moveStrategy = when (npc.strategy) {
                    LevelOuterClass.MoveStrategy.NEUTRAL -> NeutralStrategy()
                    LevelOuterClass.MoveStrategy.AGGRESSIVE -> AggressiveStrategy()
                    LevelOuterClass.MoveStrategy.COWARD -> CowardStrategy()
                    else -> NeutralStrategy()
                }
                for (item in npc.dropList) {
                    mob.drop.add(processItem(item))
                }
                if (npc.isConfused) {
                    return MobDecorator(mob, level)
                }
            }
            if (mob is Player) {
                mob.xp = stander.player.xp
                mob.level = stander.player.level
            }

            return mob
        }

        private fun getMobCharacteristics(mob: Mob, protoMob: LevelOuterClass.Mob) {
            mob.armor = protoMob.armor
            mob.criticalChance = protoMob.criticalChance
            mob.damage = protoMob.damage
            mob.resistChance = protoMob.resistChance
            mob.hp = protoMob.hp
            mob.visibilityDepth = protoMob.visibilityDepth
        }

        private fun processInventory(protoInventory: InventoryOuterClass.Inventory): Inventory {
            val inventory = Inventory()
            for (item in protoInventory.equippedList) {
                val type = item.wearable.type
                inventory.equipped[WearableType.valueOf(type)] = processItem(item) as Wearable
            }
            for (item in protoInventory.unequippedList) {
                inventory.unequipped.add(processItem(item))
            }
            return inventory
        }

        private fun processItem(protoItem: InventoryOuterClass.Item): Item {
            val id = protoItem.id
            val description = protoItem.description
            val name = protoItem.name
            return when (id) {
                "B" -> {
                    val wearable = protoItem.wearable
                    val type = WearableType.valueOf(wearable.type)
                    val builder = WearableBuilder(description, name, type)
                    builder.criticalChance = wearable.criticalChance
                    builder.damage = wearable.damage
                    builder.armor = wearable.armor
                    builder.resistChance = wearable.resistChance
                    builder.build()
                }
                else -> object : Item(description, name, id) {
                    override fun interact(level: Level) {
                    }

                }
            }
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