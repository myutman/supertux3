package ru.hse.supertux3.levels

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.hse.supertux3.logic.mobs.Player
import ru.hse.supertux3.logic.mobs.Snowball
import ru.hse.supertux3.logic.mobs.strategy.AggressiveStrategy
import ru.hse.supertux3.logic.mobs.strategy.Move
import java.io.File

class LevelTest {

    var arena = loadArena()

    @Before
    fun load() {
        arena = loadArena()
    }

    private fun loadArena(): Level {
        if (!File("src/test/resources/arena.kek").exists()) {
            val level = Level(1, 16, 16)
            level.save("src/test/resources/arena.kek")
        }
        return Level.load("src/test/resources/arena.kek")
    }

    @Test
    fun mobsGenerating() {
        val level = LevelLoader().generateLevel()
        assertTrue(level.mobs.isNotEmpty())
    }
  
    @Test
    fun saveWorks() {
        val generator = LevelLoader()
        val level = generator.generateLevel()
        val testFile = "src/test/resources/testLevel1.kek"
        level.save(testFile)
        File(testFile).delete()
    }


    @Test
    fun loadWorks() {
        val generator = LevelLoader()
        val level = generator.generateLevel()
        val testFile = "src/test/resources/testLevel1.kek"
        level.save(testFile)
        Level.load(testFile)
        File(testFile).delete()
    }

    @Test
    fun loadWorksCheckIdentity() {
        val depth = 4
        val width = 10
        val height = 10
        val level = LevelGenerator.generate(depth, height, width)
        val testFile = "src/test/resources/testLevel1.kek"
        level.save(testFile)
        val loadedLevel = Level.load(testFile)
        for (h in 0 until depth - 1) {
            for (i in 0 until height) {
                for (j in 0 until width) {
                    val cell = level.getCell(i, j, h)
                    val cellLoaded = loadedLevel.getCell(i, j, h)
                    assertTrue(cellLoaded::class == cell::class)
                    assertEquals(cell.coordinates, cellLoaded.coordinates)
                }
            }
        }
        File(testFile).delete()
    }

    @Test
    fun mobsStrategy() {
        val cell1 = arena.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val cell2 = arena.getCell(1 + player.visibilityDepth, 1, 0)
        val mob = Snowball(cell2)
        (cell2 as Floor).stander = mob
        println(arena)
        assertEquals(Move(Direction.UP, 1), AggressiveStrategy().move(arena, mob))
    }

    @Test
    fun mobsStop() {
        val cell1 = arena.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val cell2 = arena.getCell(1 + player.visibilityDepth + 1, 1, 0)
        val mob = Snowball(cell2)
        (cell2 as Floor).stander = mob
        println(arena)
        assertEquals(0, AggressiveStrategy().move(arena, mob).r)
    }


}