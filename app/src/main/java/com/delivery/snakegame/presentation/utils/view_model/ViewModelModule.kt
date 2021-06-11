package com.delivery.snakegame.presentation.utils.view_model

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import dagger.Module
import dagger.Provides

@Module
object ViewModelModule {
    @Provides
    fun provideViewModelProvider(viewModelStore: ViewModelStore, factory: ViewModelFactory) =
        ViewModelProvider(
            viewModelStore,
            factory
        )
}
