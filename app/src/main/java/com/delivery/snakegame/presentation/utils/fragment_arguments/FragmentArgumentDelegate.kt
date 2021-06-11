package com.delivery.snakegame.presentation.utils.fragment_arguments

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import kotlin.reflect.KProperty

/**
 * Eases the Fragment.newInstance ceremony by marking the fragment's args with this delegate
 * Just write the property in newInstance and read it like any other property after the fragment has been created
 */
class FragmentArgumentDelegate<T : Any> : kotlin.properties.ReadWriteProperty<Fragment, T> {

    var value: T? = null

    override operator fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        if (value == null) {
            val args = thisRef.arguments
                ?: throw IllegalStateException("Cannot read property ${property.name} if no arguments have been set")
            @Suppress("UNCHECKED_CAST")
            value = args.get(property.name) as T
        }
        return value ?: throw IllegalStateException("Property ${property.name} could not be read")
    }

    override operator fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        (thisRef.arguments ?: Bundle()).also {
            it.putAll(bundleOf(property.name to value))
            thisRef.arguments = it
        }
    }
}
