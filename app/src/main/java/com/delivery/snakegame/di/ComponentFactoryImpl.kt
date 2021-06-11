package com.delivery.snakegame.di

import android.content.Context
import javax.inject.Inject
import kotlin.reflect.KClass

typealias ComponentHolder<T> = () -> T

open class ComponentFactoryImpl @Inject constructor(
    protected val context: Context,
) : ComponentFactory {

    protected val componentMap: MutableMap<Any, ComponentHolder<Any>> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    override fun <T : Any> get(kClass: KClass<T>): T =
        componentMap[kClass]?.invoke() as? T
            ?: throw IllegalArgumentException("Wrong api class $kClass for component map")

}
