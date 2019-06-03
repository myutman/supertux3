package ru.hse.supertux3.multiplayer

import ru.hse.supertux3.CommandOuterClass
import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.levels.Ladder
import ru.hse.supertux3.levels.LevelLoader
import ru.hse.supertux3.logic.GameState
import ru.hse.supertux3.logic.Model
import ru.hse.supertux3.logic.items.WearableType
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
    private var takeTurnBarrier = CyclicBarrier(2)
    private var makeTurnBarrier = CyclicBarrier(2)

    private val usersPlay = mutableListOf<Int>()

    private var curTurnPlayer = 0

    private var usersJoin = mutableListOf<Int>()

    private fun newUser(): Int {
        val userId = nextId++
        usersJoin.add(userId)
        return userId
    }

    private val currentTurn = mutableListOf<Cell>()

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

    private fun applyCommand(command: CommandOuterClass.Command): List<Cell> {
        val userId = command.userId
        val curPlayer = level.players[userId]
        val changed = ArrayList<Cell>()
        val model = Model(GameState(level, curPlayer))
        val modelCommand: Command = if (command.hasLoot()) {
            changed.add(curPlayer.cell)
            LootCommand(model)
        } else if (command.hasMove()) {
            changed.add(curPlayer.cell)
            val direction = when (command.move.direction) {
                CommandOuterClass.Direction.UP -> Direction.UP
                CommandOuterClass.Direction.DOWN -> Direction.DOWN
                CommandOuterClass.Direction.LEFT -> Direction.LEFT
                CommandOuterClass.Direction.RIGHT -> Direction.RIGHT
                else -> null
            }
            val data = curPlayer.processMove(direction!!, level)
            changed.add(level.getCell(data.destination))
            MoveCommand(model, direction)
        } else if (command.hasStay()) {
            StayCommand(model)
        } else if (command.hasMoveLadder()) {
            changed.add(curPlayer.cell)
            changed.add(level.getCell((curPlayer.cell as Ladder).destination))
            MoveLadderCommand(model)
        } else if (command.hasPutOn()) {
            changed.add(curPlayer.cell)
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
            changed.add(curPlayer.cell)
            TakeOffCommand(model, type!!)
        } else {
            EmptyCommand()
        }
        modelCommand.execute()
        return changed
    }

    /**
     * Make turn using command
     */
    fun makeTurn(userId: Int, command: CommandOuterClass.Command): List<Cell> {
        println("MAKE TURN userId=$userId")
        currentTurn.clear()
        currentTurn.addAll(applyCommand(command))
        if (curTurnPlayer == usersPlay.size) {
            goNextCycle()
            currentTurn.addAll(moveMobs())
        }
        curTurnPlayer++
        makeTurnBarrier.await()
        return currentTurn
    }

    private fun moveMobs(): List<Cell> {
        val changed = ArrayList<Cell>()
        for (mob in level.mobs) {
            level.bfs(mob.coordinates, 1) {
                changed.add(it)
            }
            mob.move(level)
        }
        return changed
    }

    private fun goNextCycle() {
        synchronized(joinCondition) {
            println("Starting next cycle")
            curTurnPlayer = 0
            usersPlay.addAll(usersJoin)
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
    fun getUpdate(): List<Cell> {
        println("GET UPDATE")
        makeTurnBarrier.await()
        return currentTurn
    }
}
