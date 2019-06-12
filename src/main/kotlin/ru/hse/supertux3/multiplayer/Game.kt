package ru.hse.supertux3.multiplayer

import ru.hse.supertux3.CommandOuterClass
import ru.hse.supertux3.levels.*
import ru.hse.supertux3.logic.GameState
import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.logic.items.WearableType
import ru.hse.supertux3.ui.FakeView
import ru.hse.supertux3.ui.commands.*
import java.util.concurrent.CyclicBarrier

/**
 * Representation of one multiplayer game
 */
class Game(val id: String) {
    /**
     * Level where all players will play
     */
    val level = LevelLoader().generateLevel()
    private var nextId = 0

    private val joinCondition = Object()
    private var takeTurnBarrier = CyclicBarrier(1)
    private var makeTurnBarrier = CyclicBarrier(1)

    private val usersPlay = mutableListOf<Int>()

    private var curTurnPlayer = 0

    private var usersJoin = mutableListOf<Int>()

    private fun newUser(): Int {
        val userId = nextId++
        usersJoin.add(userId)
        return userId
    }

    private val currentTurn = mutableListOf<Coordinates>()

    /**
     * Starting created game with all joined users
     */
    fun start(): Int {
        synchronized(joinCondition) {
            val userId = newUser()
            println("START userId=$userId")
            goNextCycle()
            return userId
        }
    }

    /**
     * Join game, awaiting for host to start
     */
    fun join(): Int {
        synchronized(joinCondition) {
            val userId = newUser()
            println("JOIN userId=$userId")
            joinCondition.wait()
            return userId
        }
    }

    /**
     * Returns true if it is a userId player turn
     */
    fun isMyTurn(userId: Int): Boolean {
        takeTurnBarrier.await()
        println("IS MY TURN userId=$userId, next turn id=${usersPlay[curTurnPlayer]}")
        return userId == usersPlay[curTurnPlayer]
    }

    private fun applyCommand(command: CommandOuterClass.Command): List<Coordinates> {
        val userId = command.userId
        val curPlayer = level.players.find { it.userId == userId } ?: return emptyList()
        println("CUR POS: (i, j, h)=${curPlayer.cell.coordinates}")
        level.players.forEach { println("userId=" + it.userId.toString() + " " + it.cell.coordinates) }
        val changed = ArrayList<Coordinates>()
        val model = Model(GameState(level, curPlayer), FakeView())
        val modelCommand: Command = if (command.hasLoot()) {
            changed.add(curPlayer.cell.coordinates)
            LootCommand(model)
        } else if (command.hasMove()) {
            changed.add(curPlayer.cell.coordinates)
            val direction = when (command.move.direction) {
                CommandOuterClass.Direction.UP -> Direction.UP
                CommandOuterClass.Direction.DOWN -> Direction.DOWN
                CommandOuterClass.Direction.LEFT -> Direction.LEFT
                CommandOuterClass.Direction.RIGHT -> Direction.RIGHT
                else -> null
            }
            changed.add(level.getCell(curPlayer.coordinates, direction!!, 1).coordinates)
            MoveCommand(model, direction)
        } else if (command.hasStay()) {
            StayCommand(model)
        } else if (command.hasMoveLadder()) {
            changed.add(curPlayer.cell.coordinates)
            changed.add(level.getCell((curPlayer.cell as Ladder).destination).coordinates)
            MoveLadderCommand(model)
        } else if (command.hasPutOn()) {
            changed.add(curPlayer.cell.coordinates)
            PutOnCommand(model, command.putOn.index)
        } else if (command.hasTakeOff()) {
            val type = when (command.takeOff.type) {
                CommandOuterClass.WearableType.HAT -> WearableType.HAT
                CommandOuterClass.WearableType.JACKET -> WearableType.JACKET
                CommandOuterClass.WearableType.GLOVES -> WearableType.GLOVES
                CommandOuterClass.WearableType.PANTS -> WearableType.PANTS
                CommandOuterClass.WearableType.SHOES -> WearableType.SHOES
                CommandOuterClass.WearableType.WEAPON -> WearableType.WEAPON
                else -> null
            }
            changed.add(curPlayer.cell.coordinates)
            TakeOffCommand(model, type!!)
        } else {
            EmptyCommand()
        }
        modelCommand.execute()
        return changed
    }

    /**
     * Returns true if player with userId is dead
     */
    fun isPlayerDead(userId: Int): Boolean = !usersPlay.contains(userId)

    /**
     * Make turn using command
     */
    fun makeTurn(userId: Int, command: CommandOuterClass.Command): List<Cell> {
        println("MAKE TURN userId=$userId")
        currentTurn.clear()
        currentTurn.addAll(applyCommand(command))
        val oldPlayersCount = level.players.size
        if (level.players.size != oldPlayersCount) {
            usersPlay.clear()
            usersPlay.addAll(level.players.map { it.userId })
            usersPlay.sort()
            takeTurnBarrier = CyclicBarrier(usersPlay.size)
            makeTurnBarrier = CyclicBarrier(usersPlay.size)
        }
        val oldBarrier = makeTurnBarrier
        curTurnPlayer++
        if (curTurnPlayer == usersPlay.size) {
            goNextCycle()
            currentTurn.addAll(moveMobs())
            curTurnPlayer = 0
        }
        oldBarrier.await()
        return currentTurn.map { level.getCell(it) }
    }

    private fun moveMobs(): List<Coordinates> {
        val changed = ArrayList<Coordinates>()
        for (mob in level.mobs) {
            val old = mob.cell.coordinates
            mob.move(level)
            val new = mob.cell.coordinates
            changed.add(old)
            changed.add(new)
        }
        return changed
    }

    private fun goNextCycle() {
        synchronized(joinCondition) {
            println("Starting next cycle")
            curTurnPlayer = 0
            usersPlay.addAll(usersJoin)
            usersPlay.sort()
            usersJoin.forEach { level.createPlayer(it) }
            usersJoin.clear()
            takeTurnBarrier = CyclicBarrier(usersPlay.size)
            makeTurnBarrier = CyclicBarrier(usersPlay.size)
            joinCondition.notifyAll()
        }
    }

    /**
     * Getting update(another player turn or mobs turns)
     */
    fun getUpdate(userId: Int): List<Cell> {
        println("GET UPDATE userId=$userId")
        makeTurnBarrier.await()
        return currentTurn.map { level.getCell(it) }
    }
}
