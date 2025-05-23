package com.tstreet.onhand

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.tstreet.onhand.core.data.di.DaggerDataComponent
import com.tstreet.onhand.core.ui.theming.AppTheme
import com.tstreet.onhand.core.ui.theming.OnHandSizes
import com.tstreet.onhand.core.ui.theming.OnHandTheme
import com.tstreet.onhand.nav.Navigation

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("[OnHand]", "MainActivity onCreate")
        val commonComponent = application.appComponent.commonComponent
        val dataComponent = DaggerDataComponent.builder().commonComponent(commonComponent).build()
        setContent {
            OnHandTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = AppTheme.colorScheme.background,
                ) {
                    Navigation(
                        commonComponent,
                        dataComponent
                    )
                }
            }
        }
    }
}