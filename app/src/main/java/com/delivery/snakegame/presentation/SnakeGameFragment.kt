package com.delivery.snakegame.presentation

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import com.delivery.snakegame.R
import com.delivery.snakegame.databinding.FragmentSnakeGameBinding
import com.delivery.snakegame.di.DaggerSnakeGameComponent
import com.delivery.snakegame.presentation.models.SnakeGameModel
import com.delivery.snakegame.presentation.utils.*
import com.delivery.snakegame.presentation.utils.fragment_arguments.FragmentNullableArgumentDelegate
import com.delivery.snakegame.presentation.utils.livedata.nonNullObserve
import com.delivery.snakegame.presentation.view.GameEventsListener
import com.delivery.snakegame.presentation.view.SnakeGamePanel
import javax.inject.Inject

internal class SnakeGameFragment : Fragment(R.layout.fragment_snake_game) {

    companion object {
        fun newInstance(model: SnakeGameModel?) =
            SnakeGameFragment().apply {
                this.model = model
            }
    }

    private var model: SnakeGameModel? by FragmentNullableArgumentDelegate()

    @Inject
    internal lateinit var viewModel: SnakeGameViewModel

    private val binding: FragmentSnakeGameBinding by viewBinding(FragmentSnakeGameBinding::bind)

    private var snakeGamePanel: SnakeGamePanel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        DaggerSnakeGameComponent.factory().create(
            requireContext(),
            viewModelStore
        ).inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.initGamePanel.nonNullObserve(viewLifecycleOwner) { gamePanelSize ->
            snakeGamePanel = context?.let {
                SnakeGamePanel(
                    context = it,
                    settings = model,
                    screenWidth = gamePanelSize.width,
                    screenHeight = gamePanelSize.height,
                )
            }

            snakeGamePanel?.let { gamePanel ->
                gamePanel.setScopeChangeListener(object : GameEventsListener {
                    override fun onScoreValueChanged(value: Int) = Unit

                    override fun onGameEnd(value: Int) {
                        viewModel.onGameEnd(value)
                    }
                })

                binding.flStartOrPauseIconContainer.setThrottleOnClickListener {
                    gamePanel.switchGameStatus()
                    viewModel.onStartOrPauseGameClicked(gamePanel.isPaused)
                }
            }

            val paramsPanelContainer: LinearLayout.LayoutParams =
                binding.snakeGamePanelContainer.layoutParams as LinearLayout.LayoutParams
            paramsPanelContainer.width = gamePanelSize.width
            paramsPanelContainer.height = gamePanelSize.height
            binding.snakeGamePanelContainer.layoutParams = paramsPanelContainer

            binding.snakeGamePanelContainer.addView(snakeGamePanel)
        }

        viewModel.onGetGameViewGroupSize(
            width = binding.flSnakeGameContentLayout.getScreenWidth(),
            height = binding.flSnakeGameContentLayout.getScreenHeight() - binding.flSnakeGameContentLayout.height
        )

        viewModel.pauseOrStartIconLiveData.nonNullObserve(viewLifecycleOwner) { iconRes ->
            context?.let {
                binding.ivStartOrPause.setImageDrawable(ContextCompat.getDrawable(it, iconRes))
            }
        }

        viewModel.showTutorial.observe(viewLifecycleOwner) {
            snakeGamePanel?.switchGameStatus()
            binding.llTutorialContainer.isVisible = true
        }

        viewModel.finishGameDialogLiveData.nonNullObserve(viewLifecycleOwner) { result ->
            if (result.needRestart) {
                binding.viewGameFinished.gameResultContainer.isVisible = false
                snakeGamePanel?.restartGame()
                viewModel.onGameRestarted()
            } else {
                binding.viewGameFinished.gameResultContainer.isVisible = true
                binding.viewGameFinished.tvSnakeCurrentResult.text =
                    getString(R.string.current_result_text, result.currentResult)

                binding.viewGameFinished.tvSnakeRecord.text =
                    getString(R.string.record_text, result.record)
            }
        }

        binding.llTutorialContainer.setThrottleOnClickListener {
            binding.llTutorialContainer.isVisible = false
            snakeGamePanel?.switchGameStatus()
        }
    }

    override fun onResume() {
        super.onResume()
        snakeGamePanel?.startGameLoop()
    }

    override fun onPause() {
        super.onPause()
        snakeGamePanel?.stopGameLoop()
    }
}
