package com.delivery.snakegame.presentation.updater

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

internal class GameStepUpdater(
    private var tickInterval: Long
) {

    interface Listener {
        fun onStepUpdated()
    }

    @Volatile
    var isGameRunning: Boolean = false
        private set

    private var listener: Listener? = null
    private var updaterJob: Job? = null
    private val updaterScope = CoroutineScope(Dispatchers.IO)

    fun start(listener: Listener) {
        this.listener = listener
        startGameUpdates()
    }

    fun updateTickInterval(tickInterval: Long) {
        this.tickInterval = tickInterval
    }

    fun stop() {
        listener = null
        updaterJob?.cancel()
        changeUpdaterStatus(false)
    }

    private fun startGameUpdates() {
        updaterJob = updaterScope.launch {
            while (isActive) {
                changeUpdaterStatus(true)
                listener?.onStepUpdated()
                delay(tickInterval)
            }
        }
    }

    @Synchronized
    private fun changeUpdaterStatus(isRunning: Boolean) {
        isGameRunning = isRunning
    }
}
