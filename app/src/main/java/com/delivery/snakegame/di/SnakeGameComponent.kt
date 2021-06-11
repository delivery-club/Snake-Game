package com.delivery.snakegame.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.delivery.snakegame.presentation.utils.view_model.ViewModelKey
import com.delivery.snakegame.presentation.utils.view_model.ViewModelModule
import com.delivery.snakegame.data.SnakeGameRepositoryImpl
import com.delivery.snakegame.domain.SnakeGameRepository
import com.delivery.snakegame.domain.SnakeGameInteractor
import com.delivery.snakegame.domain.SnakeGameInteractorImpl
import com.delivery.snakegame.presentation.SnakeGameFragment
import com.delivery.snakegame.presentation.SnakeGameViewModel
import com.delivery.snakegame.presentation.SnakeGameViewModelImpl
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Component(
    modules = [
        SnakeGameModule::class,
        ViewModelModule::class
    ]
)
internal interface SnakeGameComponent : BaseInjector<SnakeGameFragment> {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context,
            @BindsInstance viewModelStore: ViewModelStore
        ): SnakeGameComponent
    }
}

@Module(includes = [SnakeGameModule.BindsModule::class])
internal object SnakeGameModule {

    private const val SNAKE_GAME_PREFERENCES_KEY = "SnakeGamePreferences"

    @Provides
    fun provideSnakeGameViewModel(viewModelProvider: ViewModelProvider): SnakeGameViewModel =
        viewModelProvider.get(SnakeGameViewModelImpl::class.java)

    @Provides
    fun providePreferences(context: Context) =
        context.getSharedPreferences(SNAKE_GAME_PREFERENCES_KEY, Context.MODE_PRIVATE)

    @Module
    abstract class BindsModule {

        @Binds
        @ViewModelKey(SnakeGameViewModelImpl::class)
        @IntoMap
        abstract fun bindSnakeGameViewModelImpl(impl: SnakeGameViewModelImpl): ViewModel

        @Binds
        abstract fun bindSnakeGameRepository(impl: SnakeGameRepositoryImpl): SnakeGameRepository

        @Binds
        abstract fun bindSnakeGameUseCase(impl: SnakeGameInteractorImpl): SnakeGameInteractor
    }
}
