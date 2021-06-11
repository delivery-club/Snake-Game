package com.delivery.snakegame.di

import kotlin.reflect.KClass

interface ComponentFactory {

    fun <T : Any> get(kClass: KClass<T>): T
}
