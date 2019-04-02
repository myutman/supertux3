package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.Coordinates
import ru.hse.supertux3.levels.Direction

/**
 * Class that has all player data, such as characteristics and inventory.
 */
class Player(
    override var hp: Int = 100,
    override var damage: Int = 10,
    override var resistChance: Int = 50,
    override var armor: Int = 5,
    override var criticalChance: Int = 50,
    override var position: Coordinates
) : Mob() {
    var xp: Int = 0

    /**
     * Function to attack NPC.
     */
    fun attack(npc: NPC) {
        // TODO
    }

    fun changePosition(direction: Direction) {

    }

    // some inventory fields will be added in future
}