package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.Model

/**
 * Command that represents looting inventory from cell. Changes game state.
 */
class LootCommand(val model: Model) : Command {
    override fun execute() {
        model.loot()
    }
}