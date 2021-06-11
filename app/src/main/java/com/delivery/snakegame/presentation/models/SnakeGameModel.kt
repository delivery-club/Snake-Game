package com.delivery.snakegame.presentation.models

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize

@Parcelize
class SnakeGameModel(
    @ColorRes val panelColor: Int? = null,
    @ColorRes val snakeColor: Int? = null,
    val snakeFoodIcons: List<Int>? = null,
    val scoreChangedStep: Int? = null,
    @ColorRes val roadPointsColor: Int? = null
) : Parcelable
