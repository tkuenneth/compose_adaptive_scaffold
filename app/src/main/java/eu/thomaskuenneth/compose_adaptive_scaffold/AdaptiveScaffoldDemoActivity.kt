package eu.thomaskuenneth.compose_adaptive_scaffold

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

class FoldableDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setContent {
                    val index = rememberSaveable { mutableStateOf(0) }
                    val destinations = listOf(
                        NavigationDestination(
                            icon = R.drawable.ic_android_black_24dp,
                            label = R.string.one
                        ),
                        NavigationDestination(
                            icon = R.drawable.ic_android_black_24dp,
                            label = R.string.two
                        ),
                        NavigationDestination(
                            icon = R.drawable.ic_android_black_24dp,
                            label = R.string.three
                        ),
                    )
                    MaterialTheme(
                        content = {
                            AdaptiveScaffold(
                                index = index,
                                destinations = destinations,
                                body = { Body() },
                                smallBody = { SmallBody() },
                                secondaryBody = { SecondaryBody() },
                                smallSecondaryBody = { SmallSecondaryBody() }
                            )
                        },
                        colorScheme = defaultColorScheme()
                    )
                }
            }
        }
    }
}

@Composable
private fun defaultColorScheme() = with(isSystemInDarkTheme()) {
    val hasDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    when (this) {
        true -> if (hasDynamicColor) {
            dynamicDarkColorScheme(context)
        } else {
            darkColorScheme()
        }
        false -> if (hasDynamicColor) {
            dynamicLightColorScheme(context)
        } else {
            lightColorScheme()
        }
    }
}

@Composable
private fun Body() {
    ColoredBoxWithText(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Red,
        text = stringResource(id = R.string.body)
    )
}

@Composable
private fun SmallBody() {
    ColoredBoxWithText(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Yellow,
        text = stringResource(id = R.string.small_body)
    )
}

@Composable
private fun SecondaryBody() {
    ColoredBoxWithText(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Green,
        text = stringResource(id = R.string.secondary_body)
    )
}

@Composable
private fun SmallSecondaryBody() {
    ColoredBoxWithText(
        modifier = Modifier
            .fillMaxSize(),
        color = Color.Blue,
        text = stringResource(id = R.string.small_secondary_body)
    )
}

@Composable
private fun ColoredBoxWithText(
    modifier: Modifier,
    color: Color,
    text: String
) {
    Box(
        modifier = modifier
            .background(color)
            .border(1.dp, Color.White)
    ) {
        Text(
            text = text
        )
    }
}
