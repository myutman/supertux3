package ru.hse.supertux3.levels

import javax.print.attribute.standard.Destination

open class Cell(val coordinates: Coordinates)

abstract class Floor(coordinates: Coordinates,
                     private val items: MutableList<Int> = emptyList<Int>().toMutableList()): Cell(coordinates) {

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
        fun empty(coordinates: Coordinates): Floor = object: Floor(coordinates) {
            override fun interact() {
                // do nothing
            }

            //override fun toString() = if (roomNumber == -1) "." else roomNumber.toString()
            override fun toString() = "."
        }

        var nextRoomNumber = 0
    }
}


class Wall(coordinates: Coordinates): Cell(coordinates) {
    fun canBreak(): Boolean {
        return false
    }

    fun breakMe() {

    }

    override fun toString() = "#"
}

class Door(coordinates: Coordinates): Floor(coordinates) {
    override fun interact() {
        // pass
    }

    override fun toString() = "O"

}

class Ladder(coordinates: Coordinates, destination: Int): Floor(coordinates) {
    override fun interact() {
        // pass
    }

    override fun toString() = "X"

}