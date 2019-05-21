package ru.hse.supertux3.multiplayer

import ru.hse.supertux3.levels.LevelLoader


class Game(val id: String) {
    val level = LevelLoader().generateLevel()
    var nextId = 0

    val joinCondition = Object()

    val users = mutableListOf<Int>()

    var curTurn = 0

    fun addUser() {
        val userId = nextId++
        users.add(userId)
        level.createPlayer(userId = userId)
    }


    @Synchronized
    fun join(userId: Int) {

    }
}