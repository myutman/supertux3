package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.MoveResult
import ru.hse.supertux3.logic.mobs.decorators.MobDecorator
import ru.hse.supertux3.logic.mobs.strategy.Move
import java.lang.Integer.max

/**
 * Interface for mobs (including player). Mob is basically any creature in game.
 * Mobs are divided in two groups:
 * - player
 * - non-playable characters.
 */
abstract class Mob(cell: Cell, id: String) : CellStander(cell, id) {
    /**
     * Base health points of creature.
     */
    abstract var hp: Int
    /**
     * Base resist chance of npc.
     */
    abstract var resistChance: Int
    /**
     * Base armor of npc.
     */
    abstract var armor: Int
    /**
     * Base damage of npc.
     */
    abstract var damage: Int
    /**
     * Base npc' chance of critical hit.
     */
    abstract var criticalChance: Int

    /**
     * Mob's visible area depth.
     */
    var visibilityDepth = 4

    /**
     * Mob's position.
     */
    fun position(): Coordinates = cell.coordinates

    /**
     * Returns if this npc is dead.
     */
    fun isDead() = hp <= 0

    /**
     * Function that processes attack of this npc on given npc.
     */
    fun attack(mob: Mob, level: Level): MoveResult {
        val n1 = (0..100).random()
        val n2 = (0..100).random()

        var baseDamage = damage
        if (criticalChance < n1) {
            baseDamage *= 2
            if (this is Player) {
                val decorator = MobDecorator(mob as NPC, level)
                val i = level.mobs.indexOf(mob)
                level.mobs[i] = decorator
            }
        }

        if (mob.resistChance < n2) {
            baseDamage /= 2
        }

        baseDamage -= mob.armor

        mob.hp -= max(0, baseDamage)

        return MoveResult.ATTACKED
    }

    private fun check(position: Coordinates, level: Level): Boolean {
        return level.canGo(position, Direction.UP, 0)
                && level.getCell(position) !is Wall
    }

    /**
     * The basic function that moves this npc.
     */
    open fun move(move: Move, level: Level): MoveResult {
        val directionToMove: Map<Direction, (Coordinates) -> Coordinates> = mapOf(
            Direction.UP to { position -> position.copy(i = position.i - move.r)},
            Direction.DOWN to { position -> position.copy(i = position.i + move.r)},
            Direction.LEFT to { position -> position.copy(j = position.j - move.r)},
            Direction.RIGHT to { position -> position.copy(j = position.j + move.r)}
        )

        val newPositionFunction = directionToMove[move.direction] ?: return MoveResult.FAILED
        val newPosition = newPositionFunction(position())

        if (!check(newPosition, level)) {
            return MoveResult.FAILED
        }

        if (newPosition == position()) {
            return MoveResult.MOVED
        }

        val newCell = level.getCell(newPosition)
        if (newCell is Floor && newCell.stander != null) {
            return attack(newCell.stander as Mob, level)
        }

        (cell as Floor).stander = null
        cell = newCell
        (cell as Floor).stander = this

        return MoveResult.MOVED
    }
}