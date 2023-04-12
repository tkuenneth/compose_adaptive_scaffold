package eu.thomaskuenneth.adaptivescaffolddemo

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import eu.thomaskuenneth.adaptivescaffold.AdaptiveScaffold
import eu.thomaskuenneth.adaptivescaffold.NavigationDestination
import kotlinx.coroutines.launch

class AdaptiveScaffoldDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setContent {
                    var index by rememberSaveable { mutableStateOf(0) }
                    var showSmallSecondaryBody by rememberSaveable { mutableStateOf(true) }
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
                                useDrawer = true,
                                index = index,
                                onSelectedIndexChange = { i -> index = i },
                                destinations = destinations,
                                body = { Body() },
                                smallBody = {
                                    SmallBody {
                                        showSmallSecondaryBody = !showSmallSecondaryBody
                                    }
                                },
                                secondaryBody = { SecondaryBody() },
                                smallSecondaryBody = if (showSmallSecondaryBody) {
                                    { SmallSecondaryBody() }
                                } else
                                    null
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
private fun SmallBody(onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        ColoredBoxWithText(
            modifier = Modifier
                .fillMaxSize(),
            color = Color.Yellow,
            text = stringResource(id = R.string.small_body)
        )
        Button(
            modifier = Modifier.padding(top = 32.dp, start = 32.dp),
            onClick = onClick
        ) {
            Text(
                text = stringResource(id = R.string.toggle)
            )
        }
    }
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
            .border(1.dp, Color.White),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}