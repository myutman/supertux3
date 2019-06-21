package ru.hse.supertux3.levels

import ru.hse.supertux3.logic.items.Item
import ru.hse.supertux3.logic.items.WearableBuilder
import ru.hse.supertux3.logic.items.WearableType
import ru.hse.supertux3.logic.items.WearableType.*
import ru.hse.supertux3.logic.items.ItemsDictionary
import kotlin.random.Random

class ItemsGenerator {

    companion object {

        fun generateItems(count: Int, playerLevel: Int): List<Item> {
            val items = mutableListOf<Item>()
            for (i in 1..count) {
                items.add(randomItem(playerLevel))
            }
            return items
        }

        private fun randomItem(playerLevel: Int): Item {
            val wearableType = WearableType.values().random()
            val (name, description) = ItemsDictionary.dictionary[wearableType]?.random()
                ?: Pair("item", "no one ll read it")
            val builder = WearableBuilder(description, name, wearableType)
            val randCritical = Random.nextInt(1 + playerLevel, 2 + 2 * playerLevel)
            val randResist = Random.nextInt(1 + playerLevel, 2 + 2 * playerLevel)
            val randDamage = Random.nextInt(1 + 2 * playerLevel, 2 + 2 * playerLevel)
            val randArmor = Random.nextInt(1 + 2 * playerLevel, 2 + 2 * playerLevel)
            when (wearableType) {
                GLOVES -> {
                    builder.armor = randArmor
                    builder.damage = randDamage
                    builder.resistChance = randResist
                    builder.criticalChance = randCritical
                }
                HAT -> {
                    builder.armor = 3 * randArmor / 2
                    builder.resistChance = randResist
                }
                JACKET -> {
                    builder.armor = 2 * randArmor
                    builder.resistChance = randResist
                }
                PANTS -> {
                    builder.armor = 3 * randArmor / 2
                    builder.resistChance = 3 * randResist / 2
                }
                SHOES -> {
                    builder.armor = randArmor
                    builder.resistChance = 2 * randResist
                }
                WEAPON -> {
                    builder.damage = 2 * randDamage
                    builder.criticalChance = 2 * randCritical
                }
            }
            return builder.build()
        }
    }
}