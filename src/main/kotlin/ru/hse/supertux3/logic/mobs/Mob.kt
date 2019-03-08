package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Coordinates

interface Mob {
    var HP: Int
    var damage: Int
    var resistChance: Int
    var armor: Int
    var criticalChance: Int

    var position: Coordinates
}