package com.delivery.snakegame.presentation.utils.livedata

import androidx.annotation.NonNull
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData

inline fun <T> LiveData<T>.nonNullObserve(
    @NonNull owner: LifecycleOwner,
    crossinline block: (T) -> Unit
) {
    observe(owner) { value ->
        value?.let { block(it) }
    }
}

fun <T> LiveData<T>.isNonInitialized() = value == null