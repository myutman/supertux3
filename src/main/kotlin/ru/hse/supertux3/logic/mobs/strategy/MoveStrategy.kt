package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Level

interface MoveStrategy {
    fun move(level: Level)
}