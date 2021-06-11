package com.delivery.snakegame.data

import android.content.SharedPreferences
import com.delivery.snakegame.domain.SnakeGameRepository
import javax.inject.Inject

internal class SnakeGameRepositoryImpl @Inject constructor(
    private val preferences: SharedPreferences
) : SnakeGameRepository {

    companion object {
        private const val SNAKE_RECORD_PREFERENCE_KEY = "snake_game_record_preference_key"
        private const val SNAKE_TUTORIAL_PREFERENCE_KEY = "snake_game_tutorial_preference_key"
    }

    override fun setRecord(value: Int) =
        preferences.edit().putInt(SNAKE_RECORD_PREFERENCE_KEY, value).apply()

    override fun getRecord(): Int =
        preferences.getInt(SNAKE_RECORD_PREFERENCE_KEY, 0)

    override fun disableToShowTutorial() {
        preferences.edit().putBoolean(SNAKE_TUTORIAL_PREFERENCE_KEY, false).apply()
    }

    override fun getNeedToShowTutorial(): Boolean =
        preferences.getBoolean(SNAKE_TUTORIAL_PREFERENCE_KEY, true)
}
