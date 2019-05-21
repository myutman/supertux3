package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.LevelOuterClass
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob

/**
 * Strategy that makes npc run towards player, if npc sees him.
 */
class AggressiveStrategy : MoveStrategy("A") {
    private val coward = CowardStrategy()
    override fun move(level: Level, mob: Mob) = coward.move(level, mob).opposite()

    override fun toProto(): LevelOuterClass.MoveStrategy {
        return LevelOuterClass.MoveStrategy.AGGRESSIVE
    }
}