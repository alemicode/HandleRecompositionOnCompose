package com.example.myapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // change its value to change composition strategy
        val systemWithRecomposition = true
        setContent {
            MyApplicationTheme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (!systemWithRecomposition) {
                        val someView = SomeViewStateWithNoRecomposition(point = 1)
                        ContactViewWithNoRecomposition(someView)
                    } else {
                        val someView = SomeViewStateWithRecomposition(point = 1)
                        ContactViewWithRecomposition(someView)
                    }

                }
            }
        }
    }
}


@Composable
fun ContactViewWithNoRecomposition(
    someView: SomeViewStateWithNoRecomposition,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(false) }
    // [SomeViewStateWithNoRecomposition] is @Stable, so it never recomposed but how to update its values?
    // to do this we need wrap it with mutableStateOf and update its value to update UI if needed
    var state = remember { mutableStateOf(someView) }
    Column(modifier) {
        ContactDetails(
            state.value.point
        )
        ToggleButton(selected, onStateChanged = {
            state.value = SomeViewStateWithNoRecomposition(
                point = 14
            )
            selected = !selected
        }
        )
    }

}

@Composable
fun ContactViewWithRecomposition(
    someView: SomeViewStateWithRecomposition,
    modifier: Modifier = Modifier
) {
    var selected by remember { mutableStateOf(false) }
    Column(modifier) {
        ContactDetails(
            someView.point
        )
        ToggleButton(selected, onStateChanged = {
            selected = !selected
        }
        )
    }
}

@Composable
fun ToggleButton(selected: Boolean, onStateChanged: (Boolean) -> Unit) {
    Switch(checked = selected, onCheckedChange = {
        onStateChanged(it)
    })

    Log.d("Stability", "ToggleButton view composed")
}

@Composable
fun ContactDetails(someView: Int) {

    Text(text = "Name: ${someView} ", modifier = Modifier.clickable {
    })

    Log.d("Stability", "ContactDetails view composed")
}

/**
 * By default var is mutable and this class should cause recomposition but it doesn't because of
 * using State annotation, by using this annotaiton and  mutableState in [ContactViewWithNoRecomposition]
 * we can manage recomposition
 * */
@Stable
data class SomeViewStateWithNoRecomposition(
    var point: Int
)

/**
 * By default var is mutable and this class should cause recomposition
 * */
data class SomeViewStateWithRecomposition(
    var point: Int
)


/**
 * By default val is immutable and this class shouldn't cause recomposition
 * */
data class ViewStateWithNoRecomposition(
    val list: Int
)

