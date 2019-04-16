package ru.hse.supertux3.logic.mobs.decorators

import ru.hse.supertux3.logic.mobs.Mob

/**
 * Decorator for mob class that allows to change mob's behaviour.
 */
class MobDecorator(val mob: Mob) : Mob(mob.cell, mob.id) {

    init {

    }

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
}