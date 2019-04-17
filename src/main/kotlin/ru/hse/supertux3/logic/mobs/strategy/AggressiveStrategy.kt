package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob

/**
 * Strategy that makes npc run towards player, if npc sees him.
 */
class AggressiveStrategy : MoveStrategy {
    private val coward = CowardStrategy()
    override fun move(level: Level, mob: Mob) = coward.move(level, mob).opposite()
}