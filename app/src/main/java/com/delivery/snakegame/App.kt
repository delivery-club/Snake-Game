package com.delivery.snakegame

import android.app.Application
import com.delivery.snakegame.di.AppWrapper
import com.delivery.snakegame.di.ApplicationComponent
import com.delivery.snakegame.di.ComponentFactory
import com.delivery.snakegame.di.DaggerApplicationComponent
import javax.inject.Inject

class App : Application(), AppWrapper {

    @Inject
    override lateinit var componentFactory: ComponentFactory

    override fun onCreate() {
        super.onCreate()

        buildApplicationComponent().inject(this)
    }

   private fun buildApplicationComponent(): ApplicationComponent = DaggerApplicationComponent.factory()
        .create(applicationContext)
}