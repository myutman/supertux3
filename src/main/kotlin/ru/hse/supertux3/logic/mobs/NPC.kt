package ru.hse.supertux3.logic.mobs

import ru.hse.supertux3.LevelOuterClass
import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.Level
import ru.hse.supertux3.logic.MoveData
import ru.hse.supertux3.logic.items.Item
import ru.hse.supertux3.logic.mobs.strategy.MoveStrategy

/**
 * Interface for non-playable characters. They must move by themselves.
 */
abstract class NPC(cell: Cell, id: String) : Mob(cell, id) {
    /**
     * Level of this npc, which should determine its stats
     */
    abstract var level: Int

    /**
     * Strategy used by this NPC to calculate moves.
     */
    abstract var moveStrategy: MoveStrategy

    /**
     * Function to move this NPC.
     */
    abstract fun move(level: Level): MoveData

    /**
     * Items, that will drop on the floor after mob's death
     */
    abstract val drop: MutableList<Item>

    override fun toProto(): LevelOuterClass.Mob {
        val mob = super.toProto()
        val npc = LevelOuterClass.NPC.newBuilder()
            .setLevel(level)
            .setStrategy(moveStrategy.toProto())
            .addAllDrop(drop.map { it.toProto() })
            .build()
        return mob.toBuilder().setNpc(npc).build()
    }
}