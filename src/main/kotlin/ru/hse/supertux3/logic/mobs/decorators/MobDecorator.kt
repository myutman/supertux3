package ru.hse.supertux3.logic.mobs.decorators

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Floor
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.MoveData
import ru.hse.supertux3.logic.MoveResult
import ru.hse.supertux3.logic.mobs.NPC
import ru.hse.supertux3.logic.mobs.strategy.Move
import ru.hse.supertux3.logic.mobs.strategy.MoveStrategy

/**
 * Decorator for npc class that allows to change npc's behaviour.
 */
class MobDecorator(val npc: NPC, level: Level) : NPC(npc.cell, "c") {

    init {
        redecorate()
    }

    val MAX_CONFUSED_TIME = 5

    var confusedTime = 0

    override var hp: Int
        get() = npc.hp
        set(value) { npc.hp = value }

    override var resistChance: Int
        get() = npc.resistChance
        set(value) { npc.resistChance = value }

    override var armor: Int
        get() = npc.armor
        set(value) { npc.armor = value }

    override var damage: Int
        get() = npc.damage
        set(value) { npc.damage = value }

    override var criticalChance: Int
        get() = npc.criticalChance
        set(value) { npc.criticalChance = value }

    override var level: Int
        get() = npc.level
        set(value) { npc.level = value }
    override var moveStrategy: MoveStrategy
        get() = npc.moveStrategy
        set(value) { npc.moveStrategy = value }


    private fun redecorate() {
        (cell as Floor).stander = null
        cell = npc.cell
        (cell as Floor).stander = this
    }

    private fun undecorate(level: Level) {
        (cell as Floor).stander = npc
        val i = level.mobs.indexOf(this)
        level.mobs[i] = npc
    }

    override fun move(level: Level): MoveData {
        val randomDirection = Direction.values().random()
        val moveData = npc.move(Move(randomDirection, 1), level)
        redecorate()
        confusedTime++
        if (confusedTime == MAX_CONFUSED_TIME) {
            undecorate(level)
        }
        return moveData
    }
}