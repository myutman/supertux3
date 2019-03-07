package ru.hse.supertux3.levels

open class Cell(val coordinates: Coordinates) {
    fun toWall(): Wall {
        val newWall = Wall(coordinates)
        coordinates.level.setCell(coordinates, newWall)
        return newWall
    }
}

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
        fun empty(coordinates: Coordinates): Cell = object: Floor(coordinates) {
            override fun interact() {
                // do nothing
            }

            override fun toString() = if (roomNumber == -1) "." else roomNumber.toString()
        }

        private var nextRoomNumber = 0
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