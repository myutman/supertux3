package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.logic.GameState

class QuitCommand(private val state: GameState, private val saveName: String) : Command {
    override fun execute() {
        state.level.save(saveName)
        state.quit()
    }
}