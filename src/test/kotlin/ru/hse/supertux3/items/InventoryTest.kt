package ru.hse.supertux3.items


import org.junit.AfterClass
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.GameState
import ru.hse.supertux3.logic.SinglePlayerModel
import ru.hse.supertux3.logic.items.Wearable
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.logic.mobs.decorators.MobDecorator
import ru.hse.supertux3.logic.mobs.strategy.Move
import ru.hse.supertux3.logic.mobs.strategy.NeutralStrategy
import ru.hse.supertux3.ui.FakeView
import java.io.File

class InventoryTest {
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
    fun dropWorks() {
        val cell1 = arena.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val cell2 = arena.getCell(2, 1, 0)
        val mob = Snowball(cell2)
        val loot = ItemsGenerator.generateItems(1, 0)
        mob.drop.addAll(loot)
        arena.mobs.add(mob)
        (cell2 as Floor).stander = mob

        player.damage = 1000
        val model = SinglePlayerModel(GameState(arena, player), FakeView())
        model.move(Direction.DOWN)
        println(arena)
        assertEquals((arena.getCell(mob.coordinates) as Floor).items, loot)
    }


    @Test
    fun putOn() {
        val cell1 = arena.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val oldDamage = player.damage
        val oldCriticalChance = player.criticalChance
        val oldResistChance = player.resistChance
        val oldArmor = player.armor
        val item = ItemsGenerator.generateItems(1, 0)[0] as Wearable
        item.putOn(player)
        assertEquals(player.armor, oldArmor + item.armor)
        assertEquals(player.criticalChance, oldCriticalChance + item.criticalChance)
        assertEquals(player.resistChance, oldResistChance + item.resistChance)
        assertEquals(player.damage, oldDamage + item.damage)
    }


    @Test
    fun putOff() {
        val cell1 = arena.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val oldDamage = player.damage
        val oldCriticalChance = player.criticalChance
        val oldResistChance = player.resistChance
        val oldArmor = player.armor
        val item = ItemsGenerator.generateItems(1, 0)[0] as Wearable
        item.putOn(player)
        item.takeOff(player)
        assertEquals(player.armor, oldArmor)
        assertEquals(player.criticalChance, oldCriticalChance)
        assertEquals(player.resistChance, oldResistChance)
        assertEquals(player.damage, oldDamage)
    }

}