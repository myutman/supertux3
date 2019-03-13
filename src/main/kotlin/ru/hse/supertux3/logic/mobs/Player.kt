package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Coordinates

/**
 * Class that has all player data, such as characteristics and inventory
 */
class Player(
    override var HP: Int = 100,
    override var damage: Int = 10,
    override var resistChance: Int = 50,
    override var armor: Int = 5,
    override var criticalChance: Int = 50,
    override var position: Coordinates
) : Mob {
    // some inventory fields will be added in future
}