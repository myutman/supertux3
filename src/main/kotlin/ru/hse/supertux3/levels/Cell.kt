package ru.hse.supertux3.levels

import com.beust.klaxon.Json

open class Cell(@Json(ignored = true) val coordinates: Coordinates, val id: String) {
    override fun toString() = id
}

abstract class Floor(coordinates: Coordinates, id: String) : Cell(coordinates, id) {
    @Json
    val items: MutableList<Int> = mutableListOf()
    @Json(ignored = true)
    var roomNumber = -1

    fun newRoom() {
        roomNumber = nextRoomNumber
        nextRoomNumber++
    }

    abstract fun interact()

    fun pickUp(): List<Int> = items

    fun drop(newItems: MutableList<Int>) {
        items.addAll(newItems)
    }

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

    override fun toString() = if (destination.h > coordinates.h) "v" else "^"
}