package ru.hse.supertux3.levels

import com.beust.klaxon.Json
import ru.hse.supertux3.logic.items.Item

/**
 * Enum for visibility state of cell.
 */
enum class Visibility {Visible, Hidden}

/**
 * Basic class for cells that form level.
 */
open class Cell(@Json(ignored = true) val coordinates: Coordinates,
                val id: String) {
    override fun toString() = id

    /**
     * Shows if this cell is visible by Player.
     */
    var visibility = Visibility.Hidden
}

/**
 * Class for cells that can be stepped on.
 */
abstract class Floor(coordinates: Coordinates, id: String) : Cell(coordinates, id) {
    /**
     * List of items laying on the floor.
     */
    @Json
    val items: MutableList<Item> = mutableListOf()

    /**
     * Number of room this floor cell belongs to.
     */
    @Json(ignored = true)
    var roomNumber = -1

    /**
     * Mob (CellStander) that stands on this cell, or null there is no npc.
     */
    @Json
    var stander: CellStander? = null

    /**
     *
     */
    fun newRoom() {
        roomNumber = nextRoomNumber
        nextRoomNumber++
    }

    /**
     * Some interaction with this floor cell.
     */
    abstract fun interact()

    fun pickUp(): List<Item> = items

    fun drop(newItems: MutableList<Item>) {
        items.addAll(newItems)
    }

    override fun toString() = stander?.id ?: id

    companion object {
        fun empty(coordinates: Coordinates): Floor = object : Floor(coordinates, ".") {

            override fun interact() {
                // do nothing
            }
        }

        fun chest(coordinates: Coordinates): Floor = object : Floor(coordinates, "&") {

            override fun interact() {
                // do nothing
            }
        }

        var nextRoomNumber = 0
    }
}

/**
 * Class for things that cover some cell (for example, mobs standing on floor).
 */
abstract class CellStander(@Json(ignored = true)var cell: Cell, val id: String) {
    @Json(ignored = true)
    val coordinates
        get() = cell.coordinates
}

class Wall(coordinates: Coordinates) : Cell(coordinates, "#") {

    fun canBreak(): Boolean {
        return false
    }

    fun breakMe() {

    }
}

class Door(coordinates: Coordinates) : Floor(coordinates, "O") {
    override fun interact() {
        // pass
    }

}

class Ladder(coordinates: Coordinates, @Json val destination: Coordinates) : Floor(coordinates, "L") {
    override fun interact() {
        // pass
    }

    override fun toString() = stander?.id ?: if (destination.h > coordinates.h) "v" else "^"
}