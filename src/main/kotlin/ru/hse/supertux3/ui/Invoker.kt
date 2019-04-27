package ru.hse.supertux3.ui

import ru.hse.supertux3.ui.commands.Command

class Invoker {
    var currentCommand: Command? = null

    fun run() {
        currentCommand?.execute()
        currentCommand = null
    }
}