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

    var level1 = Level.load("src/test/resources/testLevel1.kek")
    var arena = Level.load("src/test/resources/arena.kek")

    @Before
    fun load() {
        level1 = Level.load("src/test/resources/testLevel1.kek")
        arena = Level.load("src/test/resources/arena.kek")
    }

    @Test
    fun save() {
        val testFile = "src/test/resources/tempLevelTest.kek"
        val generator = LevelLoader()
        val level = generator.generateLevel()
        level.save(testFile)
        File(testFile).delete()
    }

    @Test
    fun bfsWorks() {
        val bfsStart = level1.getCell(2, 2, 0).coordinates
        level1.bfs(bfsStart, 15) {
            if (it is Floor && it !is Door) {
                it.stander = Snowball(it)
            }
        }
        println(level1)
    }

    @Test
    fun mobsGenerating() {
        val level = LevelLoader().generateLevel()
        assertTrue(level.mobs.isNotEmpty())
    }

    @Test
    fun mobsStrategy() {
        val cell1 = level1.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val cell2 = level1.getCell(1 + player.visibilityDepth, 1, 0)
        val mob = Snowball(cell2)
        (cell2 as Floor).stander = mob
        println(level1)
        assertEquals(Move(Direction.UP, 1), AggressiveStrategy().move(level1, mob))
    }

    @Test
    fun mobsStop() {
        val cell1 = level1.getCell(1, 1, 0)
        val player = Player(cell1)
        (cell1 as Floor).stander = player
        val cell2 = level1.getCell(1 + player.visibilityDepth + 1, 1, 0)
        val mob = Snowball(cell2)
        (cell2 as Floor).stander = mob
        println(level1)
        assertEquals(0, AggressiveStrategy().move(level1, mob).r)
    }

    @Test
    fun arena() {
        val cell = arena.getCell(1, 1, 0)
        val mob = Snowball(cell)
        (cell as Floor).stander = mob
        println(arena)
        arena.save("src/test/resources/arenaTest.kek")
    }

}