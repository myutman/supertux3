package ru.hse.supertux3.mob

import org.junit.AfterClass
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.logic.mobs.decorators.MobDecorator
import ru.hse.supertux3.logic.mobs.strategy.AggressiveStrategy
import ru.hse.supertux3.logic.mobs.strategy.CowardStrategy
import ru.hse.supertux3.logic.mobs.strategy.Move
import ru.hse.supertux3.logic.mobs.strategy.NeutralStrategy
import java.io.File

class DecoratorTest {
    var arena = loadArena()

    @Before
    fun load() {
        arena = loadArena()
    }
    companion object {
        @JvmStatic
        @AfterClass
        fun deleteArena() {
            File("src/test/resources/arena.kek").delete()
        }
    }

    private fun loadArena(): Level {
        if (!File("src/test/resources/arena.kek").exists()) {
            val level = Level(1, 16, 16)
            level.save("src/test/resources/arena.kek")
        }
        return Level.load("src/test/resources/arena.kek")
    }


    @Test
    fun confusionWorks() {
        val cell1 = arena.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val cell2 = arena.getCell(2, 1, 0)
        val mob = Snowball(cell2)
        arena.mobs.add(mob)
        (cell2 as Floor).stander = mob
        player.criticalChance = 100
        player.move(Move(Direction.DOWN, 1), arena)
        println(arena)
        val decorator = (arena.getCell(mob.coordinates) as Floor).stander
        assertTrue(decorator is MobDecorator)
    }

    @Test
    fun confusionCausesRandomWalk() {
        val cell1 = arena.getCell(2, 3, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val cell2 = arena.getCell(3, 3, 0)
        val mob = Snowball(cell2)
        mob.moveStrategy = NeutralStrategy()
        arena.mobs.add(mob)
        (cell2 as Floor).stander = mob
        player.criticalChance = 100
        player.move(Move(Direction.DOWN, 1), arena)
        player.move(Move(Direction.RIGHT, 1), arena)
        println(arena)
        val decorator = (arena.getCell(mob.coordinates) as Floor).stander!!
        assertTrue(decorator is MobDecorator)
        val oldCoordinates = mob.coordinates.copy()
        assertEquals(arena.mobs[0], decorator)
        arena.mobs[0].move(arena)
        println(arena)
        assertTrue(decorator.coordinates != oldCoordinates)
    }


}