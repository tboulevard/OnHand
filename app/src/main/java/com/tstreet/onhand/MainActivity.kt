package com.tstreet.onhand

import android.app.Fragment
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.tstreet.onhand.core.data.repository.IngredientSearchRepository
import com.tstreet.onhand.ui.theme.OnHandTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var repository: IngredientSearchRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (this.application as OnHandApplication)
            .appComponent
            .ingredientSearchComponent()
            .create()
            .inject(this)

        setContent {
            OnHandTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Greeting(repository.searchIngredients("")[0].name)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    OnHandTheme {
        Greeting("Android")
    }
}

class Frag : Fragment() {


}