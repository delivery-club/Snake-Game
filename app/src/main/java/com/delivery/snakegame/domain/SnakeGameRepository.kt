package com.delivery.snakegame.domain

internal interface SnakeGameRepository {
    fun setRecord(value: Int)
    fun getRecord(): Int
    fun disableToShowTutorial()
    fun getNeedToShowTutorial(): Boolean
}
