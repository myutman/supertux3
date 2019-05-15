package ru.hse.supertux3.ui.commands

import ru.hse.supertux3.ui.View

class SlideDownCommand(val view: View) : Command {
    override fun execute() {
        view.slideDown()
    }

}
