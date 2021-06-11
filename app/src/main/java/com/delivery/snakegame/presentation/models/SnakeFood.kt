package com.delivery.snakegame.presentation.models

import android.graphics.Canvas
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.Drawable
import kotlin.random.Random

internal class SnakeFood(
    private val size: Int,
    private val drawableList: List<Drawable>,
    private val positionsRectArray: Array<Array<RectF>>,
    snakeDefaultPosition: Point
) {

    lateinit var point: Point

    private val rect: Rect
        get() {
            val rect = Rect()
            rectF.round(rect)
            return rect
        }

    private val rectF: RectF
        get() = RectF(
            point.x.toFloat(),
            point.y.toFloat(),
            (point.x + size).toFloat(),
            (point.y + size).toFloat()
        )

    private var foodDrawable: Drawable

    init {
        setRandomCoordinateForFood(arrayListOf(snakeDefaultPosition))

        foodDrawable = getRandomDrawable()
    }

    fun draw(canvas: Canvas) {
        foodDrawable.bounds = rect
        foodDrawable.draw(canvas)
    }

    fun reposition(snakeTail: ArrayList<Point>) {
        foodDrawable = getRandomDrawable()
        setRandomCoordinateForFood(snakeTail)
    }

    fun intersect(snake: Snake): Boolean {
        return rect.intersect(snake.rect)
    }

    private fun getRandomDrawable(): Drawable {
        val drawableRandomPosition = Random.nextInt(drawableList.size)
        return drawableList[drawableRandomPosition]
    }

    private fun setRandomCoordinateForFood(snakeTail: ArrayList<Point>) {
        var countOfAttemptGenerate = 0
        while (countOfAttemptGenerate < 4) {
            countOfAttemptGenerate++
            val randomPoint = getRandomPoint()
            if (!snakeTail.contains(randomPoint)) {
                point = randomPoint
                return
            }
        }
        point = getRandomPoint()
    }

    private fun getRandomPoint(): Point {
        val randomColumn = Random.nextInt(positionsRectArray[0].size - 2)
        val randomLine = Random.nextInt(positionsRectArray.size - 2)
        val randomPosition = positionsRectArray[randomLine][randomColumn]
        return Point(randomPosition.left.toInt(), randomPosition.top.toInt())
    }
}
