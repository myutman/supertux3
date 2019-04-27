package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Player

/**
 * Class for keeping game state stuff, like level (game field) and player data.
 * It is used by View to draw level, player and everything else.
 */
data class GameState(val level: Level, val player: Player) {
    private var isQuit = false
    fun quit() {
        isQuit = true
    }

    fun isGameFinished() = player.isDead() || isQuit
}
