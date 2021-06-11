package com.delivery.snakegame.domain

import javax.inject.Inject

internal interface SnakeGameInteractor {
    fun updateAndGetNewRecord(newResult: Int): Int
    val isNeedToShowTutorial: Boolean
}

internal class SnakeGameInteractorImpl @Inject constructor(
    private val snakeGameRepository: SnakeGameRepository
) : SnakeGameInteractor {

    override val isNeedToShowTutorial: Boolean =
        if (snakeGameRepository.getNeedToShowTutorial()) {
            snakeGameRepository.disableToShowTutorial()
            true
        } else {
            false
        }

    override fun updateAndGetNewRecord(newResult: Int): Int {
        val lastRecord = snakeGameRepository.getRecord()
        return if (newResult > lastRecord) {
            snakeGameRepository.setRecord(newResult)
            newResult
        } else {
            lastRecord
        }
    }
}
