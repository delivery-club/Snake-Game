package com.delivery.snakegame.presentation.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.os.Parcelable
import android.provider.Settings
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.core.content.ContextCompat
import com.delivery.snakegame.R
import com.delivery.snakegame.presentation.models.Snake
import com.delivery.snakegame.presentation.models.SnakeFood
import com.delivery.snakegame.presentation.models.SnakeGameModel
import com.delivery.snakegame.presentation.updater.GameStepUpdater
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Exception
import java.util.concurrent.atomic.AtomicBoolean

internal interface GameEventsListener {
    fun onScoreValueChanged(value: Int)
    fun onGameEnd(value: Int)
}

internal class SnakeGamePanel(
    context: Context,
    private val settings: SnakeGameModel?,
    private val screenWidth: Int,
    private val screenHeight: Int
) : SurfaceView(context) {

    companion object {
        private const val DEFAULT_VALUE_SCORE_CHANGE = 1
        private const val MIN_GAME_SPEED = 350L
        private const val MAX_GAME_SPEED = 180L
        private const val DEFAULT_GAME_LEVEL_SPEED = 10L

        private const val BITE_SOUND_FILE_NAME = "bite.ogg"
        private const val LOSE_SOUND_FILE_NAME = "lose.ogg"

        private const val ZEN_MODE = "zen_mode"
        private const val ZEN_MODE_OFF = 0

        const val ROAD_POINT_RADIUS = 4
        const val MODEL_ITEM_DEFAULT_SIZE = 80
    }

    private var gameStepUpdater: GameStepUpdater = GameStepUpdater(MIN_GAME_SPEED)

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main + Job())

    private var soundPool: SoundPool? = null
    private var soundLose: Int? = null
    private var soundBite: Int? = null
    private var soundLoseIsLoaded = false
    private var soundBiteIsLoaded = false

    private lateinit var snake: Snake
    private lateinit var snakeFood: SnakeFood
    private var panelColor: Int
    private var scoreChangeStep: Int
    private val paintRoadPoint: Paint

    private var currentScoreValue = 0

    private var gameEventsListener: GameEventsListener? = null

    private var gameStepInterval = MIN_GAME_SPEED

    var isPaused = false

    private val surfaceHolderCallBackImpl = object : SurfaceHolder.Callback {
        override fun surfaceCreated(holder: SurfaceHolder) {
            startGameLoop()
        }

        override fun surfaceDestroyed(holder: SurfaceHolder) {
            stopGameLoop()
        }

        override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) =
            Unit
    }

    init {
        holder.addCallback(surfaceHolderCallBackImpl)

        gameStepUpdater = GameStepUpdater(gameStepInterval)

        isFocusable = true
        isFocusableInTouchMode = true
        requestFocus()

        setWillNotDraw(true)

        initSnakeAndFood()

        panelColor =
            ContextCompat.getColor(context, settings?.panelColor ?: R.color.background_inverted)
        scoreChangeStep = settings?.scoreChangedStep ?: DEFAULT_VALUE_SCORE_CHANGE

        paintRoadPoint = Paint()
        paintRoadPoint.color =
            ContextCompat.getColor(context, settings?.roadPointsColor ?: R.color.grey_400)

        initSounds()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            snake.handleTouchInput(event)
            return true
        }
        return false
    }

    fun setScopeChangeListener(listener: GameEventsListener) {
        gameEventsListener = listener
    }

    fun switchGameStatus() {
        isPaused = !isPaused
    }

    @Synchronized
    fun startGameLoop() {
        if (snake.isEnabled.get() && !gameStepUpdater.isGameRunning && holder.surface.isValid) {
            gameStepUpdater.start(object : GameStepUpdater.Listener {
                override fun onStepUpdated() {
                    changeGameProgress()
                }
            })
        }
    }

    fun stopGameLoop() {
        gameStepUpdater.stop()
    }

    fun restartGame() {
        initSnakeAndFood()
        currentScoreValue = 0
        gameStepInterval = MIN_GAME_SPEED
        gameStepUpdater.updateTickInterval(gameStepInterval)
        startGameLoop()
        redrawCanvas()
    }

    private fun updateSnakeSpeed() {
        gameStepInterval = if (gameStepInterval > MAX_GAME_SPEED) {
            gameStepInterval - DEFAULT_GAME_LEVEL_SPEED
        } else {
            gameStepInterval
        }
        gameStepUpdater.updateTickInterval(gameStepInterval)
    }

    private fun changeGameProgress() {
        if (!isPaused) {
            if (snake.checkBoundsCollision(this)) {
                snake.isEnabled = AtomicBoolean(false)
            }
            snake.move()

            if (snake.checkHeadAndTailCollision()) {
                snake.isEnabled = AtomicBoolean(false)
            }

            if (snakeFood.intersect(snake)) {
                tryToPlayBiteSound()
                snake.grow()
                currentScoreValue += scoreChangeStep
                gameEventsListener?.onScoreValueChanged(currentScoreValue)
                snakeFood.reposition(snake.tailPos)
                updateSnakeSpeed()
            }
        }

        redrawCanvas()
    }

    private fun tryToPlayBiteSound() {
        if (soundBiteIsLoaded) {
            soundBite?.let {
                soundPool?.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
            }
        }
    }

    private fun tryToPlayLoseSound() {
        if (soundLoseIsLoaded) {
            soundLose?.let {
                soundPool?.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
            }
        }
    }

    private fun redrawCanvas() {
        coroutineScope.launch {
            holder.lockCanvas()?.let { canvas ->
                canvas.drawColor(panelColor)
                drawRoadPoints(canvas)

                if (snake.isEnabled.get()) {
                    snake.draw(canvas)
                    snakeFood.draw(canvas)
                } else {
                    tryToPlayLoseSound()
                    gameEventsListener?.onGameEnd(currentScoreValue)
                    stopGameLoop()
                }
                holder.unlockCanvasAndPost(canvas)
            }
        }
    }

    private fun drawRoadPoints(canvas: Canvas) {
        for (height in ROAD_POINT_RADIUS..screenHeight + ROAD_POINT_RADIUS step MODEL_ITEM_DEFAULT_SIZE) {
            for (width in ROAD_POINT_RADIUS..screenWidth + ROAD_POINT_RADIUS step MODEL_ITEM_DEFAULT_SIZE) {
                canvas.drawCircle(
                    width.toFloat(),
                    height.toFloat(),
                    ROAD_POINT_RADIUS.toFloat(),
                    paintRoadPoint
                )
            }
        }
    }

    private fun initSnakeAndFood() {
        val roadsRectArray = getGameRoadsRectArray()
        initSnake(roadsRectArray)
        initSnakeFood(roadsRectArray)
    }

    private fun initSnake(roadsRectArray: Array<Array<RectF>>) {
        snake = Snake(
            snakeSize = MODEL_ITEM_DEFAULT_SIZE,
            snakeColor = ContextCompat.getColor(context, settings?.snakeColor ?: R.color.green_dc),
            positionsRectArray = roadsRectArray
        )
    }

    private fun initSnakeFood(roadsRectArray: Array<Array<RectF>>) {
        snakeFood = SnakeFood(
            size = MODEL_ITEM_DEFAULT_SIZE,
            drawableList = settings?.snakeFoodIcons.takeIf { !it.isNullOrEmpty() }?.map {
                ContextCompat.getDrawable(context, it)!!
            } ?: getDefaultSnakeFoodList(),
            positionsRectArray = roadsRectArray,
            snakeDefaultPosition = snake.point
        )
    }

    private fun getGameRoadsRectArray(): Array<Array<RectF>> {
        var roadsRectArray = arrayOf<Array<RectF>>()

        for (height in ROAD_POINT_RADIUS until screenHeight + ROAD_POINT_RADIUS step MODEL_ITEM_DEFAULT_SIZE) {
            var lineArray = arrayOf<RectF>()
            for (width in ROAD_POINT_RADIUS until screenWidth + ROAD_POINT_RADIUS step MODEL_ITEM_DEFAULT_SIZE) {
                val rect = RectF(
                    width.toFloat(),
                    height.toFloat(),
                    (width + MODEL_ITEM_DEFAULT_SIZE).toFloat(),
                    (height + MODEL_ITEM_DEFAULT_SIZE).toFloat()
                )
                lineArray += rect
            }
            roadsRectArray += lineArray
        }
        return roadsRectArray
    }

    private fun getDefaultSnakeFoodList(): List<Drawable> {
        val foodList = mutableListOf<Drawable>()
        ContextCompat.getDrawable(context, R.drawable.food_1)?.let {
            foodList.add(it)
        }
        ContextCompat.getDrawable(context, R.drawable.food_2)?.let {
            foodList.add(it)
        }
        ContextCompat.getDrawable(context, R.drawable.food_3)?.let {
            foodList.add(it)
        }
        ContextCompat.getDrawable(context, R.drawable.food_4)?.let {
            foodList.add(it)
        }
        return foodList
    }

    private fun initSounds() {
        try {
            if (Settings.Global.getInt(context.contentResolver, ZEN_MODE) == ZEN_MODE_OFF) {
                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                soundPool = SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .build()

                soundBite = soundPool?.load(context.assets.openFd(BITE_SOUND_FILE_NAME), 1)
                soundLose = soundPool?.load(context.assets.openFd(LOSE_SOUND_FILE_NAME), 1)

                soundPool?.setOnLoadCompleteListener { _, soundId, _ ->
                    when (soundId) {
                        soundBite -> soundBiteIsLoaded = true
                        soundLose -> soundLoseIsLoaded = true
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("initSounds", e.localizedMessage.orEmpty())
        }
    }
}
