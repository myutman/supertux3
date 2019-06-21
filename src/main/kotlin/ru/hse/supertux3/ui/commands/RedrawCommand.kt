package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.ui.View

/**
 * Command that represents redrawing all screen.
 */
class RedrawCommand(private val view: View) : Command {
    override fun execute() {
        view.redraw()
    }
}