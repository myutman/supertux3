package ru.hse.supertux3.levels

import org.junit.Assert.*
import org.junit.Test

class LevelTest {
    @Test
    fun saveWorks() {
        val generator = LevelLoader()
        val level = generator.generateLevel()
        level.save("src/test/resources/testLevel1.kek")
    }


    @Test
    fun loadWorks() {
        val generator = LevelLoader()
        val level = generator.generateLevel()
        level.save("src/test/resources/testLevel1.kek")
        Level.load("src/test/resources/testLevel1.kek")
    }

    @Test
    fun loadWorksCheckIdentity() {
        val depth = 4
        val width = 10
        val height = 10
        val level = LevelGenerator.generate(depth, height, width)
        level.save("src/test/resources/testLevel1.kek")
        val loadedLevel = Level.load("src/test/resources/testLevel1.kek")
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
    }
}