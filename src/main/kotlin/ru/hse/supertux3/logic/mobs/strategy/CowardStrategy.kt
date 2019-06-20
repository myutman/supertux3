package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.LevelOuterClass
import ru.hse.supertux3.levels.Coordinates
import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Floor
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob
import ru.hse.supertux3.logic.mobs.Player

/**
 * Strategy that makes npc run away from player, if npc sees him.
 */
class CowardStrategy : MoveStrategy() {
    override fun move(level: Level, mob: Mob): Move {
        var playerCoordinates: Coordinates? = null

        level.bfs(mob.coordinates, mob.visibilityDepth) {
            if (it is Floor && it.stander != null && it.stander is Player) {
                playerCoordinates = it.coordinates
            }
        }
        if (playerCoordinates == null) {
            return Move(Direction.RIGHT, 0)
        } else {
            val distance = level.bfs(playerCoordinates!!, mob.visibilityDepth + 1) {}
            for (direction in Direction.values()) {
                if (level.canGo(mob.coordinates, direction, 1)) {
                    val next = level.getCell(mob.coordinates, direction, 1)
                    if (next.coordinates in distance && distance[next.coordinates]!! > distance[mob.coordinates]!!) {
                        return Move(direction, 1)
                    }
                }
            }
            return Move(Direction.RIGHT, 0)
        }
    }

    override fun toProto(): LevelOuterClass.MoveStrategy {
        return LevelOuterClass.MoveStrategy.COWARD
    }
}