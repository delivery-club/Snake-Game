package com.delivery.snakegame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.delivery.snakegame.presentation.SnakeGameFragment
import com.delivery.snakegame.presentation.models.SnakeGameModel

class SnakeActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snake)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.snake_container, SnakeGameFragment.newInstance(SnakeGameModel()))
            .commit();
    }
}