@file:Suppress("UNCHECKED_CAST")

package com.tstreet.onhand.core.common

import android.os.Bundle
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.remember
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel

/**
 * Per: https://www.droidcon.com/2021/06/06/dagger-2-and-jetpack-compose-integration/
 *
 * tl;dr allows us to attach ViewModels to lifecycle of parent activity/fragment
 *
 * NOTE: Dropped in favor of [injectedViewModel] - this needs some tweaking to work with
 *       @Composable functions
 */
@Composable
@Deprecated("Potentially in favor of below injectedViewModel - research")
inline fun <reified T : ViewModel> daggerViewModel(
    key: String? = null,
    crossinline viewModelInstanceCreator: () -> T
): T =
    androidx.lifecycle.viewmodel.compose.viewModel(
        modelClass = T::class.java,
        key = key,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModelInstanceCreator() as T
            }
        }
    )

/**
 * Per:
 * https://www.droidcon.com/2021/12/14/navigating-through-multi-module-jetpack-compose-applications/
 */
@Composable
inline fun <reified VM : ViewModel> injectedViewModel(
    key: String? = null,
    crossinline viewModelInstanceCreator: @DisallowComposableCalls () -> VM
): VM {
    val factory = remember(key) {
        object : ViewModelProvider.Factory {
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                @Suppress("UNCHECKED_CAST")
                return viewModelInstanceCreator() as VM
            }
        }
    }
    return viewModel(key = key, factory = factory)
}

@Composable
inline fun <reified VM : ViewModelWithBundle> injectedViewModelWithArguments(
    bundle: Bundle? = null,
    key: String? = null,
    crossinline viewModelInstanceCreator: @DisallowComposableCalls () -> VM,
): VM {
    val factory = remember(key) {
        object : ViewModelFactoryWithBundle(bundle) {
            override fun <VM : ViewModel> create(modelClass: Class<VM>): VM {
                @Suppress("UNCHECKED_CAST")
                val viewModel = viewModelInstanceCreator() as VM
                println("[OnHand] ViewModelFactory, setting bundle as: ${theBundle.toString()}")
                // TODO: bundle is set after VM is created, so
                (viewModel as ViewModelWithBundle).bundle = theBundle
                println("[OnHand] This viewModel instance (injected src):$viewModel")
                return viewModel
            }
        }
    }
    return viewModel(key = key, factory = factory)
}

abstract class ViewModelWithBundle : ViewModel() {
    var bundle: Bundle? = null
}

abstract class ViewModelFactoryWithBundle(
    val theBundle: Bundle?
) : ViewModelProvider.Factory

