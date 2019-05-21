package ru.hse.supertux3.multiplayer

import ru.hse.supertux3.levels.LevelLoader
import ru.hse.supertux3.logic.mobs.Player


class Game(val id: String) {
    val level = LevelLoader().generateLevel()

    val users = mutableListOf<Int>()

    var curTurn = 0

    init {
        for (userId in users) {
            level.createPlayer(userId)
        }
    }
}