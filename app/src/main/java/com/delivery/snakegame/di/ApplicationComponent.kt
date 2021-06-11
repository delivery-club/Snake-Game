package com.delivery.snakegame.di

import android.content.Context
import com.delivery.snakegame.App
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ApplicationScope

@Component(
    modules = [
        ApplicationBindsModule::class
    ]
)
@ApplicationScope
interface ApplicationComponent : BaseInjector<App> {

    @Component.Factory
    interface Factory {
        fun create(
            @BindsInstance context: Context
        ): ApplicationComponent
    }
}

@Module
abstract class ApplicationBindsModule {

    @Binds
    abstract fun bindComponentFactory(componentFactoryImpl: ComponentFactoryImpl): ComponentFactory
}
