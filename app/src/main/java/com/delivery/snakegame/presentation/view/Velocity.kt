package com.delivery.snakegame.presentation.view

import com.delivery.snakegame.presentation.models.MovingDirectionEnum

class Velocity(
    var xSpeed: Float = 5f,
    var ySpeed: Float = 5f
) {

    var xDirection: Int
        private set
    var yDirection: Int
        private set

    init {
        xDirection = MovingDirectionEnum.DIRECTION_RIGHT.value
        yDirection = MovingDirectionEnum.DIRECTION_DOWN.value
    }

    fun setXSpeed(xv: Float): Velocity {
        xSpeed = xv
        return this
    }

    fun setYSpeed(yv: Float): Velocity {
        ySpeed = yv
        return this
    }

    fun setXDirection(xDirection: Int): Velocity {
        this.xDirection = xDirection
        return this
    }

    fun setYDirection(yDirection: Int): Velocity {
        this.yDirection = yDirection
        return this
    }

    fun stop(): Velocity {
        setXSpeed(0f)
        setYSpeed(0f)
        return this
    }
}
