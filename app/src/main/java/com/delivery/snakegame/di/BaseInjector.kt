package com.delivery.snakegame.di

interface BaseInjector<T> {

    fun inject(injected: T)
}
