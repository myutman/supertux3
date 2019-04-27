package ru.hse.supertux3.levels

import org.junit.Test
import org.junit.Assert.*

class LevelGeneratorTest {
    @Test
    fun roomsTest() {
        val depth = 4
        val width = 10
        val height = 10
        for (i in 0..20) {
            val level = LevelGenerator.generate(depth, height, width)
            for (h in 0 until depth) {
                val cell = level.getCell(1, 1, h)
                assertTrue(cell is Floor)
                assertTrue((cell as Floor).roomNumber != -1 || cell is Ladder || cell.id == "&") // chest
            }
        }
    }

    @Test
    fun wallsTest() {
        val depth = 4
        val width = 10
        val height = 10
        for (t in 0..20) {
            val level = LevelGenerator.generate(depth, height, width)
            for (h in 0 until depth) {
                for (i in 0 until height) {
                    assertTrue(level.getCell(i, 0, h) is Wall)
                    assertTrue(level.getCell(i, width - 1, h) is Wall)
                }
                for (j in 0 until width) {
                    assertTrue(level.getCell(0, j, h) is Wall)
                    assertTrue(level.getCell(height - 1, j, h) is Wall)
                }
            }
        }
    }
}