package ru.hse.supertux3.levels

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import ru.hse.supertux3.logic.mobs.Snowball
import java.io.File

class LevelTest {

    var level1 = Level.load("src/test/resources/testLevel1.kek")

    @Before
    fun load() {
        level1 = Level.load("src/test/resources/testLevel1.kek")
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

}