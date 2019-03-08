package ru.hse.supertux3.ui

import com.github.ajalt.mordant.TermColors
import org.jline.terminal.Attributes
import org.jline.terminal.TerminalBuilder

val height = 10
val width = 10

val field = Array(height) {
    CharArray(width) {
        '.'
    }
}

var i = 0
var j = 0

fun TermColors.init() {
    field[0][0] = '@'
    print("\u001Bc")
    for (line in field) {
        println(rgb("ff0000")(line.joinToString("")))
    }
    print(cursorUp(height))
    print(strikethrough)
}

fun check(i: Int, j: Int): Boolean {
    return i >= 0 && i < height && j >= 0 && j < width
}

fun TermColors.moveUp() {
    if (check(i - 1, j)) {
        field[i][j] = '.'
        i--
        field[i][j] = '@'

        print('.')
        print(cursorLeft(1))
        print(cursorUp(1))
        print('@')
        print(cursorLeft(1))
    }
}

fun TermColors.moveDown() {
    if (check(i + 1, j)) {
        field[i][j] = '.'
        i++
        field[i][j] = '@'

        print('.')
        print(cursorLeft(1))
        print(cursorDown(1))
        print('@')
        print(cursorLeft(1))
    }
}

fun TermColors.moveLeft() {
    if (check(i, j - 1)) {
        field[i][j] = '.'
        j--
        field[i][j] = '@'

        print('.')
        print(cursorLeft(1))
        print(cursorLeft(1))
        print('@')
        print(cursorLeft(1))

    }
}

fun TermColors.moveRight() {
    if (check(i, j + 1)) {
        field[i][j] = '.'
        j++
        field[i][j] = '@'

        print('.')
        print(cursorLeft(1))
        print(cursorRight(1))
        print(red("@"))
        print(cursorLeft(1))
    }
}

fun main() = TermColors(TermColors.Level.TRUECOLOR).run {
    val attributes = Attributes()
    //attributes.setLocalFlag(Attributes.LocalFlag.ECHOE, true)
    attributes.setLocalFlag(Attributes.LocalFlag.ECHO, false)
    val terminal = TerminalBuilder.builder()
        .jna(true)
        .system(true)
        .attributes(attributes)
        .build()
    terminal.enterRawMode()
    val reader = terminal.reader()

    init()

    while (true) {

        val buffer = CharArray(4)
        var read = 0
        while (read == 0) {
            read = reader.read(buffer)
        }

        var quit = false
        when (buffer[0]) {
            'q' -> quit = true
            'w' -> moveUp()
            'a' -> moveLeft()
            'd' -> moveRight()
            's' -> moveDown()
        }

        if (quit) {
            break
        }
    }
}