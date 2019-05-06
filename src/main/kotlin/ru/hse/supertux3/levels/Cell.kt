package ru.hse.supertux3.levels

import com.beust.klaxon.Json

/**
 * Base class for cell that is the simplest part of level
 * @param coordinates coordinates in level
 * @param id unique id, it is used in console UI
 */
open class Cell(@Json(ignored = true) val coordinates: Coordinates, val id: String) {
    override fun toString() = id
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
    val items: MutableList<Int> = mutableListOf()

    /**
     * Number of room that contains this floor, mostly for generation needs
     */
    @Json(ignored = true)
    var roomNumber = -1

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
}

/**
 * Mobs can stand in this cells
 */
class Wall(coordinates: Coordinates) : Cell(coordinates, "#")

/**
 * Mobs can go through it
 */
class Door(coordinates: Coordinates) : Floor(coordinates, "O")

/**
 * You can stand on it, but you can go to another stage or level with it
 */
class Ladder(coordinates: Coordinates, @Json val destination: Coordinates) : Floor(coordinates, "L") {
    override fun toString() = if (destination.h > coordinates.h) "v" else "^"
}