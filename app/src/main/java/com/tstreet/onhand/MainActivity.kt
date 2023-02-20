package com.tstreet.onhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.CompositionLocalProvider
import com.tstreet.onhand.core.data.di.LocalDataProvider
import com.tstreet.onhand.nav.Navigation
import com.tstreet.onhand.ui.theme.OnHandTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OnHandTheme {
                CompositionLocalProvider(
                    LocalDataProvider provides application.appComponent.dataComponent,
                ) {
                    Navigation()
                }
            }
        }
    }
}