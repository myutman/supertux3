package ru.hse.supertux3.levels

import org.junit.Test
import org.junit.Assert.*

class LevelGeneratorTest {
    private val depth = 4
    private val width = 20
    private val height = 20
    @Test
    fun roomsTest() {
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

    @Test
    fun laddersTest() {
        for (t in 0..20) {
            val level = LevelGenerator.generate(depth, height, width)
            val connected = BooleanArray(depth - 1) { false }
            for (h in 0 until depth - 1) {
                for (i in 0 until height) {
                    for (j in 0 until width) {
                        val cell = level.getCell(i, j, h)
                        if (cell is Ladder) {
                            connected[h] = true
                        }
                    }
                }
            }
            for (stage in connected) {
                assertTrue(stage)
            }
        }
    }
}