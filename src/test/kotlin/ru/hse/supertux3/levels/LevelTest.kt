package ru.hse.supertux3.levels

import org.junit.Assert.*
import org.junit.Test

class LevelTest {
    @Test
    fun save() {
        val generator = LevelLoader()
        val level = generator.generateLevel()
        level.save("src/test/resources/testLevel1.kek")
        println(Level.load("src/test/resources/testLevel1.kek"))
    }
}