package com.delivery.snakegame.presentation.utils.fragment_arguments

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

class FragmentNullableArgumentDelegate<T : Any> :
    kotlin.properties.ReadWriteProperty<Fragment, T?> {

    var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T? {
        if (value == null) {
            val args = thisRef.arguments
                ?: throw IllegalStateException("Cannot read property ${property.name} if no arguments have been set")
            @Suppress("UNCHECKED_CAST")
            value = args.get(property.name) as T?
        }
        return value
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T?) {
        (thisRef.arguments ?: Bundle()).also {
            it.putAll(bundleOf(property.name to value))
            thisRef.arguments = it
        }
    }
}
