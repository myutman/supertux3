package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.ui.View

/**
 * Command that represents sliding the unworn inventory list down.
 */
class SlideDownCommand(val view: View) : Command {
    override fun execute() {
        view.slideDown()
    }

}
