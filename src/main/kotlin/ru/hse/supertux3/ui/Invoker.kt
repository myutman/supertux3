package ru.hse.supertux3.ui

import ru.hse.supertux3.ui.commands.Command

/**
 * Class that executes command and than remove it
 */
class Invoker {

    /**
     * Command to execute
     */
    var currentCommand: Command? = null

    /**
     * Execute command and remove it
     */
    fun run() {
        currentCommand?.execute()
        currentCommand = null
    }
}