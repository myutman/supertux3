package ru.hse.supertux3.levels

import com.beust.klaxon.Json
import ru.hse.supertux3.LevelOuterClass
import ru.hse.supertux3.logic.items.Item

/**
 * Enum for visibility state of cell.
 */
enum class Visibility { Visible, Hidden }

/**
 * Base class for cell that is the simplest part of level
 * @param coordinates coordinates in level
 * @param id unique id, it is used in console UI
 */
open class Cell(@Json(ignored = true) val coordinates: Coordinates, val id: String) {
    override fun toString() = id

    /**
     * Shows if this cell is visible by Player.
     */
    var visibility = Visibility.Hidden

    open fun toProto(): LevelOuterClass.Cell {
        return LevelOuterClass.Cell.newBuilder()
            .setId(id)
            .setCoordinates(coordinates.toProto())
            .build()
    }
}

/**
 * Floor is just a cell with additional features:
 * 1) Any mob can stand here
 * 2) Some items can lie here
 */
open class Floor(coordinates: Coordinates, id: String) : Cell(coordinates, id) {
    /**
     * List of items that lie in the floor
     */
    @Json
    val items: MutableList<Item> = mutableListOf()

    /**
     * Number of room that contains this floor, mostly for generation needs
     */
    @Json(ignored = true)
    var roomNumber = -1

    /**
     * Mob (CellStander) that stands on this cell, or null there is no npc.
     */
    @Json(ignored = true)
    var stander: CellStander? = null

    override fun toString() = stander?.id ?: if (items.isEmpty()) id else "l"

    companion object {
        /**
         * Creates empty floor
         */
        fun empty(coordinates: Coordinates): Floor = Floor(coordinates, ".")

        /**
         * Creates floor with chest(it's useless, cause we don't need to maintain items)
         */
        fun chest(coordinates: Coordinates): Floor = Floor(coordinates, "&")
    }

    override fun toProto(): LevelOuterClass.Cell {
        val cell = super.toProto().toBuilder()
        val curStander = stander
        return if (curStander != null) {
            cell.setStander(curStander.toProto()).build()
        } else {
            cell.build()
        }
    }
}

/**
 * Class for things that cover some cell (for example, mobs standing on floor).
 */
abstract class CellStander(@Json(ignored = true) var cell: Cell, val id: String) {
    @Json(ignored = true)
    val coordinates
        get() = cell.coordinates

    open fun toProto(): LevelOuterClass.CellStander {
        return LevelOuterClass.CellStander.newBuilder().setId(id).build()
    }
}


/**
 * Mobs cant stand in this cells
 */
class Wall(coordinates: Coordinates) : Cell(coordinates, "#")

/**
 * Mobs can go through it
 */
class Door(coordinates: Coordinates) : Floor(coordinates, "O")

/**
 * You can stand on it, but you can also go to another stage or level with it
 */
class Ladder(coordinates: Coordinates, @Json val destination: Coordinates) : Floor(coordinates, "L") {
    override fun toString() = stander?.id ?: if (destination.h > coordinates.h) "v" else "^"

    override fun toProto(): LevelOuterClass.Cell {
        val cell = super.toProto()
        val ladder = LevelOuterClass.Ladder.newBuilder()
            .setDestinationCoordinates(destination.toProto())
            .setLevelId(destination.levelId)
            .build()
        return cell.toBuilder().setLadder(ladder).build()
    }
}