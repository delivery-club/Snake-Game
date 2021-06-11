package com.delivery.snakegame.presentation

import android.os.CountDownTimer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.delivery.snakegame.R
import com.delivery.snakegame.presentation.utils.livedata.SingleLiveEvent
import com.delivery.snakegame.domain.SnakeGameInteractor
import com.delivery.snakegame.presentation.models.GamePenalSize
import com.delivery.snakegame.presentation.models.GameResult
import com.delivery.snakegame.presentation.utils.livedata.isNonInitialized
import com.delivery.snakegame.presentation.view.SnakeGamePanel
import javax.inject.Inject

internal interface SnakeGameViewModel {
    val pauseOrStartIconLiveData: LiveData<Int>
    val finishGameDialogLiveData: LiveData<GameResult>
    val showTutorial: LiveData<Unit>
    val initGamePanel: LiveData<GamePenalSize>

    fun onGetGameViewGroupSize(width: Int, height: Int)
    fun onGameEnd(value: Int)
    fun onStartOrPauseGameClicked(isPaused: Boolean)
    fun onGameRestarted()
}

internal class SnakeGameViewModelImpl @Inject constructor(
    private val snakeGameInteractor: SnakeGameInteractor
) : ViewModel(), SnakeGameViewModel {

    companion object {
        private const val GAME_RESTART_TIME = 3000L
        private const val GAME_RESTART_TIME_TICK = 1000L
    }

    override val pauseOrStartIconLiveData: MutableLiveData<Int> = MutableLiveData()
    override val finishGameDialogLiveData: SingleLiveEvent<GameResult> = SingleLiveEvent()
    override val showTutorial: SingleLiveEvent<Unit> = SingleLiveEvent()
    override val initGamePanel: MutableLiveData<GamePenalSize> = MutableLiveData()

    init {
        if (snakeGameInteractor.isNeedToShowTutorial) {
            showTutorial.call()
        }
    }

    override fun onGetGameViewGroupSize(width: Int, height: Int) {
        if (initGamePanel.isNonInitialized()) {
            val widthDivideReminder = width % SnakeGamePanel.MODEL_ITEM_DEFAULT_SIZE
            val heightDivideReminder = height % SnakeGamePanel.MODEL_ITEM_DEFAULT_SIZE
            val newWidth = width - widthDivideReminder + SnakeGamePanel.ROAD_POINT_RADIUS * 2
            val newHeight = height - heightDivideReminder + SnakeGamePanel.ROAD_POINT_RADIUS * 2
            initGamePanel.value = GamePenalSize(newWidth, newHeight)
        }
    }

    override fun onGameEnd(value: Int) {
        val record = snakeGameInteractor.updateAndGetNewRecord(value)
        finishGameDialogLiveData.value = GameResult(record = record, currentResult = value)
        startGameFinishTimer()
    }

    override fun onStartOrPauseGameClicked(isPaused: Boolean) {
        pauseOrStartIconLiveData.value = if (isPaused) {
            R.drawable.ic_play
        } else {
            R.drawable.ic_pause
        }
    }

    override fun onGameRestarted() {
        finishGameDialogLiveData.value = null
    }

    private fun startGameFinishTimer() {
        val timer = object : CountDownTimer(GAME_RESTART_TIME, GAME_RESTART_TIME_TICK) {
            override fun onTick(millisUntilFinished: Long) = Unit

            override fun onFinish() {
                finishGameDialogLiveData.value = GameResult(needRestart = true)
            }
        }
        timer.start()
    }
}
