package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.ui.View

class RedrawCommand(private val view: View) : Command {
    override fun execute() {
        view.redraw()
    }
}