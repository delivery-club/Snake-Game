package com.delivery.snakegame.presentation.utils

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.delivery.snakegame.di.AppWrapper
import com.delivery.snakegame.di.ComponentFactory

fun FragmentActivity.getComponentFactory(): ComponentFactory =
    (application as AppWrapper).componentFactory

fun Fragment.getComponentFactory(): ComponentFactory =
    requireActivity().getComponentFactory()
