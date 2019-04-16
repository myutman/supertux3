package ru.hse.supertux3.logic.mobs.decorators

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Floor
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.MoveResult
import ru.hse.supertux3.logic.mobs.Mob
import ru.hse.supertux3.logic.mobs.NPC
import ru.hse.supertux3.logic.mobs.strategy.Move

/**
 * Decorator for mob class that allows to change mob's behaviour.
 */
class MobDecorator(val mob: Mob, level: Level) : NPC(mob.cell, "c") {

    init {
        redecorate()
    }

    val MAX_CONFUSED_TIME = 5

    var confusedTime = 0

    override var hp: Int
        get() = mob.hp
        set(value) { mob.hp = value }

    override var resistChance: Int
        get() = mob.resistChance
        set(value) { mob.resistChance = value }

    override var armor: Int
        get() = mob.armor
        set(value) { mob.armor = value }

    override var damage: Int
        get() = mob.damage
        set(value) { mob.damage = value }

    override var criticalChance: Int
        get() = mob.criticalChance
        set(value) { mob.criticalChance = value }

    private fun redecorate() {
        cell = mob.cell
        (cell as Floor).stander = this
    }

    private fun undecorate(level: Level) {
        (cell as Floor).stander = mob
        val i = level.mobs.indexOf(this)
        level.mobs[i] = mob
    }

    override fun move(level: Level): MoveResult {
        val randomDirection = Direction.values().random()
        val moveResult = mob.move(Move(randomDirection, 1), level)
        redecorate()
        confusedTime++
        if (confusedTime == MAX_CONFUSED_TIME) {
            undecorate(level)
        }
        return moveResult
    }
}