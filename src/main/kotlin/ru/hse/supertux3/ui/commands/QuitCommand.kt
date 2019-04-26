package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.GameState

class QuitCommand(private val state: GameState) : Command {
    override fun execute() {
        state.level.save("kek")
        state.quit()
    }
}