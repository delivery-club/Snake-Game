package com.delivery.snakegame.presentation.models

import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Canvas
import android.view.MotionEvent
import com.delivery.snakegame.presentation.view.SnakeGamePanel
import com.delivery.snakegame.presentation.view.Velocity
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.math.abs

internal class Snake(
    snakeColor: Int,
    private val snakeSize: Int,
    private val positionsRectArray: Array<Array<RectF>>
) {

    companion object {
        private const val COMPARE_EPSILON = 80f
        private const val SNAKE_DEFAULT_POSITION_ROAD_LINE = 3
        private const val SNAKE_DEFAULT_POSITION_ROAD_COLUMN = 4
    }

    val rect: Rect
        get() {
            val rect = Rect()
            rectF.round(rect)
            return rect
        }

    var isEnabled: AtomicBoolean = AtomicBoolean(true)

    var tailPos: ArrayList<Point>

    var point: Point

    private val rectF: RectF
        get() = RectF(
            point.x.toFloat(),
            point.y.toFloat(),
            (point.x + snakeSize).toFloat(),
            (point.y + snakeSize).toFloat()
        )

    private val velocity: Velocity = Velocity()

    private val paint: Paint = Paint()

    private var currentDirectionMoving = MovingDirectionEnum.DIRECTION_RIGHT

    init {
        changeMovingDirection()
        point = getSnakeDefaultPosition()

        tailPos = ArrayList()

        stylePaint(paint, snakeColor)
    }

    fun draw(canvas: Canvas) {
        canvas.drawRect(rect, paint)
        for (p in tailPos) {
            val r = Rect(p.x, p.y, p.x + snakeSize, p.y + snakeSize)
            canvas.drawRect(r, paint)
        }
    }

    fun move() {
        if (isEnabled.get()) {
            val headX = point.x
            val headY = point.y
            if (tailPos.isNotEmpty()) {
                for (x in tailPos.size - 1 downTo 1) {
                    tailPos[x][tailPos[x - 1].x] = tailPos[x - 1].y
                }
                tailPos[0][headX] = headY
            }

            if (movingDirectionChangingEnabled(point)) {
                changeMovingDirection()
            }

            point.x += (velocity.xSpeed * velocity.xDirection).toInt()
            point.y += (velocity.ySpeed * velocity.yDirection).toInt()
        }
    }

    fun grow() {
        tailPos.add(Point(point.x, point.y))
    }

    fun checkBoundsCollision(panel: SnakeGamePanel): Boolean = when {
        point.x < 0 -> {
            true
        }
        point.x >= panel.width - snakeSize -> {
            true
        }
        point.y < 0 -> {
            true
        }
        point.y >= panel.height - snakeSize -> {
            true
        }
        else -> false
    }

    fun checkHeadAndTailCollision(): Boolean {
        val headPoint = Point(point.x, point.y)
        return tailPos.contains(headPoint)
    }

    fun handleTouchInput(event: MotionEvent) {
        if (velocity.ySpeed == 0f) {
            if (event.y < point.y) {
                currentDirectionMoving = MovingDirectionEnum.DIRECTION_UP
            } else if (event.y > point.y && velocity.ySpeed == 0f) {
                currentDirectionMoving = MovingDirectionEnum.DIRECTION_DOWN
            }
        } else if (velocity.xSpeed == 0f) {
            if (event.x < point.x) {
                currentDirectionMoving = MovingDirectionEnum.DIRECTION_LEFT
            } else if (event.x > point.x) {
                currentDirectionMoving = MovingDirectionEnum.DIRECTION_RIGHT
            }
        }
    }

    private fun stylePaint(paint: Paint, color: Int) {
        paint.color = color
        paint.style = Paint.Style.FILL
    }

    private fun getSnakeDefaultPosition(): Point =
        Point(
            positionsRectArray[SNAKE_DEFAULT_POSITION_ROAD_LINE][SNAKE_DEFAULT_POSITION_ROAD_COLUMN].left.toInt(),
            positionsRectArray[SNAKE_DEFAULT_POSITION_ROAD_LINE][SNAKE_DEFAULT_POSITION_ROAD_COLUMN].top.toInt()
        )

    private fun movingDirectionChangingEnabled(snakeHead: Point): Boolean =
        positionsRectArray.find { columnsArray ->
            columnsArray.find {
                abs(it.left - snakeHead.x) < COMPARE_EPSILON &&
                    abs(it.top - snakeHead.y) < COMPARE_EPSILON
            } != null
        } != null

    private fun changeMovingDirection() {
        when (currentDirectionMoving) {
            MovingDirectionEnum.DIRECTION_UP,
            MovingDirectionEnum.DIRECTION_DOWN -> {
                velocity.stop().setYDirection(currentDirectionMoving.value).ySpeed = snakeSize.toFloat()
            }
            MovingDirectionEnum.DIRECTION_LEFT,
            MovingDirectionEnum.DIRECTION_RIGHT -> {
                velocity.stop().setXDirection(currentDirectionMoving.value).xSpeed = snakeSize.toFloat()
            }
        }
    }
}
