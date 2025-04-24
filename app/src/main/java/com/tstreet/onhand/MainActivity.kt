package com.tstreet.onhand

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.tstreet.onhand.core.data.impl.di.DaggerDataComponent
import com.tstreet.onhand.core.ui.theming.OnHandTheme
import com.tstreet.onhand.nav.Navigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // To make status bar color match app background color...
            window.statusBarColor = ContextCompat.getColor(this, R.color.Grey10)
            OnHandTheme {
                val commonComponent = remember { application.appComponent.commonComponent }
                val dataComponent = remember { DaggerDataComponent.builder().commonComponent(commonComponent).build() }
                Navigation(
                    commonComponent,
                    dataComponent
                )
            }
        }
    }
}