package ru.hse.supertux3.logic.mobs.strategy

import ru.hse.supertux3.levels.Coordinates
import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Floor
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.mobs.Mob
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball

class CowardStrategy : MoveStrategy {
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
            val mob = mob.coordinates
            val player: Coordinates = playerCoordinates as Coordinates
            if (player.i < mob.i) {
                return Move(Direction.DOWN, 1)
            } else if (player.i > mob.i) {
                return Move(Direction.UP, 1)
            } else if (player.j < mob.j) {
                return Move(Direction.RIGHT, 1)
            } else {
                return Move(Direction.LEFT, 1)
            }
        }

    }
}