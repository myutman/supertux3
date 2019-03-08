package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Player

data class GameState(val level: Level, val player: Player)