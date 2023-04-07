package eu.thomaskuenneth.compose_adaptive_scaffold

import android.app.Activity
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.window.core.ExperimentalWindowApi
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator

data class NavigationDestination(
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val enabled: Boolean = true,
    val alwaysShowLabel: Boolean = true,
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalWindowApi::class)
@Composable
fun Activity.AdaptiveScaffold(
    index: MutableState<Int>,
    destinations: List<NavigationDestination> = emptyList(),
    body: @Composable () -> Unit = {},
    smallBody: @Composable () -> Unit = {},
    secondaryBody: @Composable () -> Unit = {},
    smallSecondaryBody: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val layoutInfo by WindowInfoTracker.getOrCreate(context)
        .windowLayoutInfo(context).collectAsState(
            initial = null
        )
    val windowMetrics = WindowMetricsCalculator.getOrCreate()
        .computeCurrentWindowMetrics(this)
    val foldDef = createFoldDef(layoutInfo, windowMetrics)
    val hasBottomBar =
        foldDef.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val hasNavigationRail = !hasBottomBar
    Scaffold(
        bottomBar = {
            AdaptiveScaffoldBottomBar(
                hasBottomBar = hasBottomBar,
                index = index,
                destinations = destinations
            )
        }
    ) { padding ->
        AdaptiveScaffoldContent(
            foldDef = foldDef,
            paddingValues = padding,
            hasNavigationRail = hasNavigationRail,
            index = index,
            destinations = destinations,
            body = body,
            smallBody = smallBody,
            secondaryBody = secondaryBody,
            smallSecondaryBody = smallSecondaryBody
        )
    }
}

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyTopBar(hasTopBar: Boolean) {
//    if (hasTopBar)
//        TopAppBar(title = {
//            Text(stringResource(id = R.string.app_name))
//        })
//}

@Composable
private fun AdaptiveScaffoldBottomBar(
    hasBottomBar: Boolean,
    index: MutableState<Int>,
    destinations: List<NavigationDestination>
) {
    if (hasBottomBar)
        NavigationBar {
            for (i in destinations.indices)
                with(destinations[i]) {
                    val label = stringResource(id = label)
                    NavigationBarItem(
                        selected = i == index.value,
                        onClick = { index.value = i },
                        icon = {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(text = label)
                        }
                    )
                }
        }
}

@Composable
fun AdaptiveScaffoldNavigationRail(
    hasNavigationRail: Boolean,
    index: MutableState<Int>,
    destinations: List<NavigationDestination>
) {
    if (hasNavigationRail)
        NavigationRail() {
            for (i in destinations.indices)
                with(destinations[i]) {
                    val label = stringResource(id = label)
                    NavigationRailItem(
                        selected = i == index.value,
                        onClick = { index.value = i },
                        icon = {
                            Icon(
                                painter = painterResource(id = icon),
                                contentDescription = label
                            )
                        },
                        label = {
                            Text(text = label)
                        }
                    )
                }
        }
}

@Composable
private fun AdaptiveScaffoldContent(
    foldDef: FoldDef,
    paddingValues: PaddingValues,
    hasNavigationRail: Boolean,
    index: MutableState<Int>,
    destinations: List<NavigationDestination>,
    body: @Composable () -> Unit,
    smallBody: @Composable () -> Unit,
    secondaryBody: @Composable () -> Unit,
    smallSecondaryBody: @Composable () -> Unit,
) {
    Row(modifier = Modifier.fillMaxSize()) {
        AdaptiveScaffoldNavigationRail(
            hasNavigationRail = hasNavigationRail,
            index = index,
            destinations = destinations,
        )
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = paddingValues)
        ) {
            if (foldDef.hasFold) {
                FoldableScreen(
                    foldDef = foldDef,
                    body = body,
                    secondaryBody = secondaryBody,
                )
            } else if (foldDef.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
                TwoPaneScreen(
                    firstPane = body,
                    secondPane = secondaryBody
                )
            } else {
                TwoPaneScreen(
                    firstPane = smallBody,
                    secondPane = smallSecondaryBody
                )
            }
        }
    }
}

@Composable
private fun TwoPaneScreen(
    firstPane: @Composable () -> Unit,
    secondPane: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            val localModifier = Modifier
                .fillMaxHeight()
                .weight(0.5F)
            Box(modifier = localModifier) {
                firstPane()
            }
            Box(modifier = localModifier) {
                secondPane()
            }
        }
    }
}

@Composable
private fun FoldableScreen(
    foldDef: FoldDef,
    body: @Composable () -> Unit,
    secondaryBody: @Composable () -> Unit,
) {
    val hinge = @Composable {
        Spacer(
            modifier = Modifier
                .width(foldDef.foldWidth)
                .height(foldDef.foldHeight)
        )
    }
    val firstComposable = @Composable {
        body()
    }
    val secondComposable = @Composable {
        secondaryBody()
    }
    val container = @Composable {
        if (foldDef.foldOrientation == FoldingFeature.Orientation.VERTICAL) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.0F)
                ) {
                    firstComposable()
                }
                hinge()
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(foldDef.widthRightOrBottom)
                ) {
                    secondComposable()
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0F)
                ) {
                    firstComposable()
                }
                hinge()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(foldDef.heightRightOrBottom)
                ) {
                    secondComposable()
                }
            }
        }
    }
    container()
}
