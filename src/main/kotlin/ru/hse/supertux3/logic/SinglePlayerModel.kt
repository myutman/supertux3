package ru.hse.supertux3.logic

import ru.hse.supertux3.levels.Direction
import ru.hse.supertux3.logic.items.WearableType
import ru.hse.supertux3.ui.ViewLike

class SinglePlayerModel(state: GameState, view: ViewLike): Model(state, view) {
    override fun loot() {
        super.loot()
        afterAction()
    }

    override fun move(direction: Direction) {
        super.move(direction)
        afterAction()
    }

    override fun moveLadder() {
        super.moveLadder()
        afterAction()
    }

    override fun putOn(index: Int) {
        super.putOn(index)
        afterAction()
    }

    override fun putOff(type: WearableType) {
        super.putOff(type)
        afterAction()
    }

    override fun selfHarm() {
        super.selfHarm()
        afterAction()
    }
}