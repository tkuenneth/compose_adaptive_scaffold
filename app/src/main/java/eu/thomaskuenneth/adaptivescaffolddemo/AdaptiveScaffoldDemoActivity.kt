package eu.thomaskuenneth.adaptivescaffolddemo

import android.os.Bundle
import androidx.activity.ComponentActivity
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
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.window.core.layout.WindowWidthSizeClass
import eu.thomaskuenneth.adaptivescaffold.AdaptiveScaffold
import eu.thomaskuenneth.adaptivescaffold.LocalFoldDef
import eu.thomaskuenneth.adaptivescaffold.NavigationDestination
import eu.thomaskuenneth.adaptivescaffold.defaultColorScheme
import eu.thomaskuenneth.adaptivescaffold.setContentRepeatOnLifecycleStarted

fun destinationOne(showSmallSecondaryBody: Boolean) = NavigationDestination(
    icon = R.drawable.ic_android_black_24dp,
    label = R.string.one,
    body = { Body() },
    smallBody = { SmallBody() },
    secondaryBody = { SecondaryBody() },
    smallSecondaryBody = if (showSmallSecondaryBody) {
        { SmallSecondaryBody() }
    } else null
)

val destinationFoldInfo = NavigationDestination(
    icon = R.drawable.baseline_perm_device_information_24,
    label = R.string.fold_info,
    overlay = { FoldDefInfo() },
)

class AdaptiveScaffoldDemoActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentRepeatOnLifecycleStarted(enableEdgeToEdge = true) {
            var toggleSmallSecondaryBodyVisible by rememberSaveable { mutableStateOf(false) }
            var showSmallSecondaryBody by rememberSaveable { mutableStateOf(true) }
            MaterialTheme(colorScheme = defaultColorScheme()) {
                AdaptiveScaffold(
                    useDrawer = true,
                    startDestination = destinationOne(showSmallSecondaryBody),
                    otherDestinations = listOf(destinationFoldInfo),
                    onDestinationChanged = {
                        toggleSmallSecondaryBodyVisible = it != destinationFoldInfo
                    },
                    topBar = { scrollBehavior ->
                        AdaptiveScaffoldDemoTopAppBar(
                            scrollBehavior = scrollBehavior,
                            toggleSmallSecondaryBodyVisible = toggleSmallSecondaryBodyVisible,
                            toggleSmallSecondaryBodyClicked = {
                                showSmallSecondaryBody = !showSmallSecondaryBody
                            }
                        )
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveScaffoldDemoTopAppBar(
    scrollBehavior: TopAppBarScrollBehavior,
    toggleSmallSecondaryBodyClicked: () -> Unit,
    toggleSmallSecondaryBodyVisible: Boolean
) {
    var moreOpen by remember { mutableStateOf(false) }
    val menuItems = mutableListOf<@Composable () -> Unit>()
    if ((toggleSmallSecondaryBodyVisible) &&
        (LocalFoldDef.current.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT)
    ) {
        menuItems.add {
            DropdownMenuItem(
                onClick = {
                    toggleSmallSecondaryBodyClicked()
                    moreOpen = false
                },
                text = { Text(stringResource(id = R.string.toggle_small_secondary_body)) }
            )
        }
    }
    TopAppBar(
        scrollBehavior = scrollBehavior,
        title = { Text(text = stringResource(id = R.string.app_name)) },
        actions = {
            if (menuItems.isNotEmpty()) {
                IconButton(onClick = { moreOpen = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = stringResource(id = R.string.options_menu)
                    )
                }
                DropdownMenu(
                    expanded = moreOpen,
                    onDismissRequest = { moreOpen = false }
                ) {
                    menuItems.forEach { it() }
                }
            }
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
private fun FoldDefInfo() {
    with(LocalFoldDef.current) {
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        ) {
            item {
                FoldDefInfoItem("isSeparating", isSeparating.toString())
                FoldDefInfoItem("orientation", orientation.toString())
                FoldDefInfoItem("occlusionType", occlusionType.toString())
                FoldDefInfoItem("state", state.toString())
                VerticalSpacer()
                FoldDefInfoItem(
                    "windowWidthSizeClass",
                    windowSizeClass.windowWidthSizeClass.toString().lastPart()
                )
                FoldDefInfoItem(
                    "windowHeightSizeClass",
                    windowSizeClass.windowHeightSizeClass.toString().lastPart()
                )
                VerticalSpacer()
                FoldDefInfoItem("foldWidth", foldWidth.toString())
                FoldDefInfoItem("foldHeight", foldHeight.toString())
                VerticalSpacer()
                FoldDefInfoItem("widthLeftOrTop", widthLeftOrTop.toString())
                FoldDefInfoItem("heightLeftOrTop", heightLeftOrTop.toString())
                VerticalSpacer()
                FoldDefInfoItem("widthRightOrBottom", widthRightOrBottom.toString())
                FoldDefInfoItem("heightRightOrBottom", heightRightOrBottom.toString())
                VerticalSpacer()
                FoldDefInfoItem("bounds", bounds.toString())
            }
        }
    }
}

@Composable
fun VerticalSpacer() {
    Spacer(modifier = Modifier.height(16.dp))
}

@Composable
fun FoldDefInfoItem(label: String, value: String) {
    Row {
        Text(
            modifier = Modifier.alignByBaseline(),
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            modifier = Modifier.alignByBaseline(),
            text = value,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

private fun String.lastPart(): String {
    val pattern = ": "
    return if (contains(pattern))
        substring(lastIndexOf(pattern) + pattern.length)
    else
        this
}
