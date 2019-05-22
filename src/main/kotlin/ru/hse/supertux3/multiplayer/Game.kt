package ru.hse.supertux3.multiplayer

import ru.hse.supertux3.CommandOuterClass
import ru.hse.supertux3.levels.Cell
import ru.hse.supertux3.levels.LevelLoader
import java.util.concurrent.CyclicBarrier


class Game(val id: String) {
    val level = LevelLoader().generateLevel()
    private var nextId = 0

    private val joinCondition = Object()
    private var takeTurnBarrier = CyclicBarrier(0)
    private var makeTurnBarrier = CyclicBarrier(0)

    private val usersPlay = mutableListOf<Int>()

    private var curTurnPlayer = 0

    private var usersJoin = mutableListOf<Int>()

    private fun newUser(): Int {
        val userId = nextId++
        usersJoin.add(userId)
        return userId
    }

    private val currentTurn = mutableListOf<Cell>()

    fun start(): Int {
        synchronized(joinCondition) {
            val userId = newUser()
            goNextCycle()
            return userId
        }
    }

    fun join(): Int {
        synchronized(joinCondition) {
            val userId = newUser()
            joinCondition.wait()
            return userId
        }
    }

    fun isMyTurn(userId: Int): Boolean {
        takeTurnBarrier.await()
        return userId == usersPlay[curTurnPlayer]
    }

    private fun applyCommand(userId: Int, command: CommandOuterClass.Command): List<Cell> {
        TODO("applying command to level, returning list of cells inside")
    }

    fun makeTurn(userId: Int, command: CommandOuterClass.Command): List<Cell> {
        currentTurn.clear()
        currentTurn.addAll(applyCommand(userId, command))
        curTurnPlayer++
        if (curTurnPlayer == usersPlay.size) {
            goNextCycle()
            moveMobs()
        }
        makeTurnBarrier.await()
        return currentTurn
    }

    private fun moveMobs() {
        TODO("moving NPC in level")
    }

    private fun goNextCycle() {
        curTurnPlayer = 0
        usersPlay.addAll(usersJoin)
        usersJoin.clear()
        takeTurnBarrier = CyclicBarrier(usersPlay.size)
        makeTurnBarrier = CyclicBarrier(usersPlay.size)
        joinCondition.notifyAll()

    }

    fun getUpdate(): List<Cell> {
        makeTurnBarrier.await()
        return currentTurn
    }
}
