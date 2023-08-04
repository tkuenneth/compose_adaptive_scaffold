package eu.thomaskuenneth.adaptivescaffolddemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import eu.thomaskuenneth.adaptivescaffold.AdaptiveScaffold
import eu.thomaskuenneth.adaptivescaffold.NavigationDestination
import eu.thomaskuenneth.adaptivescaffold.defaultColorScheme
import eu.thomaskuenneth.adaptivescaffold.setContentRepeatOnLifecycleStarted

val destination1 = NavigationDestination(
    icon = R.drawable.ic_android_black_24dp,
    label = R.string.one,
    body = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Red)
        )
    },
    secondaryBody = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Green)
        )
    },
    smallBody = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Blue)
        )
    },
    smallSecondaryBody = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.Yellow)
        )
    },
)

val destination2 = NavigationDestination(
    icon = R.drawable.ic_android_black_24dp,
    label = R.string.two,
    overlay = {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.LightGray)
        )
    },
)

class SimpleDemoActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentRepeatOnLifecycleStarted {
            MaterialTheme(
                content = {
                    AdaptiveScaffold(
                        startDestination = destination1,
                        otherDestinations = listOf(destination2),
                        onDestinationChanged = {
                            // do something
                        },
                        topBar = {
                            TopAppBar(
                                title = {
                                    Text(
                                        text = stringResource(
                                            id = R.string.app_name
                                        )
                                    )
                                })
                        },
                    )
                },
                colorScheme = defaultColorScheme()
            )
        }
    }
}
