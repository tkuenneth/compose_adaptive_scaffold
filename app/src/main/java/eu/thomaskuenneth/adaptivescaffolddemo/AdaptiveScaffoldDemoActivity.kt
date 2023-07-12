package eu.thomaskuenneth.adaptivescaffolddemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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

fun destinationOne(showSmallSecondaryBody: Boolean) = NavigationDestination(
    icon = R.drawable.ic_android_black_24dp,
    label = R.string.one,
    body = {
        Body()
    },
    smallBody = {
        SmallBody()
    },
    secondaryBody = { SecondaryBody() },
    smallSecondaryBody = if (showSmallSecondaryBody) {
        { SmallSecondaryBody() }
    } else null
)

val destinationFoldInfo = NavigationDestination(
    icon = R.drawable.baseline_perm_device_information_24,
    label = R.string.fold_info
)

class AdaptiveScaffoldDemoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                setContent {
                    var showSmallSecondaryBody by rememberSaveable { mutableStateOf(true) }
                    MaterialTheme(
                        content = {
                            AdaptiveScaffold(
                                useDrawer = true,
                                startDestination = destinationOne(showSmallSecondaryBody),
                                otherDestinations = listOf(destinationFoldInfo),
                                topBar = {
                                    AdaptiveScaffoldDemoTopAppBar(
                                        showSmallSecondaryBodyClicked = {
                                            showSmallSecondaryBody =
                                                !showSmallSecondaryBody
                                        }
                                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveScaffoldDemoTopAppBar(showSmallSecondaryBodyClicked: () -> Unit) {
    var moreOpen by remember { mutableStateOf(false) }
    TopAppBar(
        title = {
            Text(text = stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(
                onClick = {
                    moreOpen = true
                }
            ) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = stringResource(id = R.string.options_menu)
                )
            }
            AdaptiveScaffoldDemoDropDownMenu(
                expanded = moreOpen,
                onDismissRequest = { moreOpen = false },
                showSmallSecondaryBodyClicked = {
                    showSmallSecondaryBodyClicked()
                    moreOpen = false
                }
            )
        }
    )
}

@Composable
private fun Body() {
    Box(modifier = Modifier.fillMaxSize()) {
        ColoredBoxWithText(
            modifier = Modifier
                .fillMaxSize(),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            text = stringResource(id = R.string.body),
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun SmallBody(
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ColoredBoxWithText(
            modifier = Modifier
                .fillMaxSize(),
            backgroundColor = MaterialTheme.colorScheme.primaryContainer,
            text = stringResource(id = R.string.small_body),
            textColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
private fun SecondaryBody() {
    ColoredBoxWithText(
        modifier = Modifier
            .fillMaxSize(),
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        text = stringResource(id = R.string.secondary_body),
        textColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
private fun SmallSecondaryBody() {
    ColoredBoxWithText(
        modifier = Modifier
            .fillMaxSize(),
        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
        text = stringResource(id = R.string.small_secondary_body),
        textColor = MaterialTheme.colorScheme.onSecondaryContainer
    )
}

@Composable
private fun ColoredBoxWithText(
    modifier: Modifier,
    backgroundColor: Color,
    text: String,
    textColor: Color
) {
    Box(
        modifier = modifier
            .background(backgroundColor)
            .border(1.dp, MaterialTheme.colorScheme.primary),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = textColor
        )
    }
}

@Composable
private fun AdaptiveScaffoldDemoDropDownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    showSmallSecondaryBodyClicked: () -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        if (LocalFoldDef.current.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
            DropdownMenuItem(
                onClick = showSmallSecondaryBodyClicked,
                text = {
                    Text(
                        text = stringResource(id = R.string.toggle_small_secondary_body)
                    )
                }
            )
        }
    }
}
