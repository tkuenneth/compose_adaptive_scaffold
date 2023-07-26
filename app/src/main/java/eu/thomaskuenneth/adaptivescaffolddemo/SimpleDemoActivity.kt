package eu.thomaskuenneth.adaptivescaffolddemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import eu.thomaskuenneth.adaptivescaffold.AdaptiveScaffold
import eu.thomaskuenneth.adaptivescaffold.LocalFoldDef
import eu.thomaskuenneth.adaptivescaffold.NavigationDestination
import eu.thomaskuenneth.adaptivescaffold.defaultColorScheme
import kotlinx.coroutines.launch

val destination1 = NavigationDestination(
    icon = R.drawable.ic_android_black_24dp,
    label = R.string.app_name,
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
    label = R.string.app_name,
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
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setContent {
                    MaterialTheme(
                        content = {
                            AdaptiveScaffold(
                                useDrawer = true,
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
    }
}
