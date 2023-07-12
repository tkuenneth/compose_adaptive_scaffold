package eu.thomaskuenneth.adaptivescaffold

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentDrawerSheet
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator

data class NavigationDestination(
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val enabled: Boolean = true,
    val alwaysShowLabel: Boolean = true,

    val body: @Composable () -> Unit = {},
    val smallBody: @Composable () -> Unit = {},
    val secondaryBody: @Composable () -> Unit = {},
    val smallSecondaryBody: (@Composable () -> Unit)? = null
)

val LocalFoldDef = compositionLocalOf { FoldDef() }

@Composable
fun Activity.AdaptiveScaffold(
    useDrawer: Boolean = false,
    startDestination: NavigationDestination,
    otherDestinations: List<NavigationDestination> = emptyList(),
    topBar: @Composable () -> Unit = {},
) {
    val destinations = listOf(startDestination).plus(otherDestinations)
    var index by rememberSaveable(
        startDestination,
        otherDestinations
    ) { mutableStateOf(destinations.indexOf(startDestination)) }
    val currentDestination = destinations[index]
    AdaptiveScaffold(
        useDrawer = useDrawer,
        index = index,
        onSelectedIndexChange = { index = it },
        destinations = destinations,
        topBar = topBar,
        body = currentDestination.body,
        secondaryBody = currentDestination.secondaryBody,
        smallBody = currentDestination.smallBody,
        smallSecondaryBody = currentDestination.smallSecondaryBody
    )
}

@Composable
fun Activity.AdaptiveScaffold(
    useDrawer: Boolean = false,
    index: Int = -1,
    onSelectedIndexChange: (Int) -> Unit = {},
    destinations: List<NavigationDestination> = emptyList(),
    topBar: @Composable () -> Unit = {},
    body: @Composable () -> Unit,
    smallBody: @Composable () -> Unit,
    secondaryBody: @Composable () -> Unit,
    smallSecondaryBody: (@Composable () -> Unit)? = null
) {
    val context = LocalContext.current
    val layoutInfo by WindowInfoTracker.getOrCreate(context)
        .windowLayoutInfo(context).collectAsState(
            initial = null
        )
    val windowMetrics = WindowMetricsCalculator.getOrCreate()
        .computeCurrentWindowMetrics(this)
    val foldDef = createFoldDef(layoutInfo, windowMetrics)
    val hasDrawer =
        useDrawer && foldDef.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.EXPANDED
    val hasBottomBar =
        foldDef.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT
    val hasNavigationRail = !hasBottomBar && !hasDrawer
    CompositionLocalProvider(LocalFoldDef provides foldDef) {
        Scaffold(
            topBar = {
                topBar()
            },
            bottomBar = {
                AdaptiveScaffoldBottomBar(
                    hasBottomBar = hasBottomBar,
                    index = index,
                    onSelectedIndexChange = onSelectedIndexChange,
                    destinations = destinations
                )
            }
        ) { padding ->
            AdaptiveScaffoldContent(
                foldDef = foldDef,
                paddingValues = padding,
                hasNavigationRail = hasNavigationRail,
                hasDrawer = hasDrawer,
                index = index,
                onSelectedIndexChange = onSelectedIndexChange,
                destinations = destinations,
                body = body,
                smallBody = smallBody,
                secondaryBody = secondaryBody,
                smallSecondaryBody = smallSecondaryBody
            )
        }
    }
}

@Composable
private fun AdaptiveScaffoldBottomBar(
    hasBottomBar: Boolean,
    index: Int,
    onSelectedIndexChange: (Int) -> Unit,
    destinations: List<NavigationDestination>
) {
    if (hasBottomBar && destinations.isNotEmpty())
        NavigationBar {
            for (i in destinations.indices)
                with(destinations[i]) {
                    val label = stringResource(id = label)
                    NavigationBarItem(
                        selected = i == index,
                        onClick = { onSelectedIndexChange(i) },
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
private fun AdaptiveScaffoldNavigationRail(
    hasNavigationRail: Boolean,
    index: Int,
    onSelectedIndexChange: (Int) -> Unit,
    destinations: List<NavigationDestination>
) {
    if (hasNavigationRail && destinations.isNotEmpty())
        NavigationRail {
            for (i in destinations.indices)
                with(destinations[i]) {
                    val label = stringResource(id = label)
                    NavigationRailItem(
                        selected = i == index,
                        onClick = { onSelectedIndexChange(i) },
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
private fun AdaptiveScaffoldDrawer(
    hasDrawer: Boolean,
    index: Int,
    onSelectedIndexChange: (Int) -> Unit,
    destinations: List<NavigationDestination>,
    content: @Composable () -> Unit
) {
    if (hasDrawer && destinations.isNotEmpty())
        PermanentNavigationDrawer(
            drawerContent = {
                PermanentDrawerSheet(Modifier.width(240.dp)) {
                    Spacer(Modifier.height(12.dp))
                    for (i in destinations.indices)
                        with(destinations[i]) {
                            val label = stringResource(id = label)
                            NavigationDrawerItem(
                                selected = i == index,
                                onClick = { onSelectedIndexChange(i) },
                                icon = {
                                    Icon(
                                        painter = painterResource(id = icon),
                                        contentDescription = label
                                    )
                                },
                                label = {
                                    Text(text = label)
                                },
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                }
            },
            content = content
        )
    else {
        content()
    }
}

@Composable
private fun AdaptiveScaffoldContent(
    foldDef: FoldDef,
    paddingValues: PaddingValues,
    hasNavigationRail: Boolean,
    hasDrawer: Boolean,
    index: Int,
    onSelectedIndexChange: (Int) -> Unit,
    destinations: List<NavigationDestination>,
    body: @Composable () -> Unit,
    smallBody: @Composable () -> Unit,
    secondaryBody: @Composable () -> Unit,
    smallSecondaryBody: (@Composable () -> Unit)?,
) {
    val content: @Composable () -> Unit = {
        Row(modifier = Modifier.fillMaxSize()) {
            AdaptiveScaffoldNavigationRail(
                hasNavigationRail = hasNavigationRail,
                index = index,
                onSelectedIndexChange = onSelectedIndexChange,
                destinations = destinations,
            )
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                if (
                    foldDef.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT &&
                    foldDef.foldOrientation == FoldingFeature.Orientation.HORIZONTAL &&
                    (foldDef.occlusionType == FoldingFeature.OcclusionType.NONE || foldDef.foldHeight <= 4.dp)
                ) {
                    TwoPaneScreen(
                        firstPane = smallBody,
                        secondPane = smallSecondaryBody,
                        maxWidth = maxWidth
                    )
                } else if (foldDef.hasFold) {
                    FoldableScreen(
                        foldDef = foldDef,
                        body = body,
                        secondaryBody = secondaryBody,
                    )
                } else if (foldDef.windowSizeClass.windowWidthSizeClass != WindowWidthSizeClass.COMPACT) {
                    TwoPaneScreen(
                        firstPane = body,
                        secondPane = secondaryBody,
                        maxWidth = maxWidth
                    )
                } else {
                    TwoPaneScreen(
                        firstPane = smallBody,
                        secondPane = smallSecondaryBody,
                        maxWidth = maxWidth
                    )
                }
            }
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues = paddingValues)
    ) {
        AdaptiveScaffoldDrawer(
            hasDrawer = hasDrawer,
            index = index,
            onSelectedIndexChange = onSelectedIndexChange,
            destinations = destinations,
            content = content
        )
    }
}

@Composable
private fun TwoPaneScreen(
    firstPane: @Composable () -> Unit,
    secondPane: (@Composable () -> Unit)?,
    maxWidth: Dp
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(1.0F)
            ) {
                firstPane()
            }
            if (secondPane != null) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .size(maxWidth / 2)
                ) {
                    secondPane()
                }
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
    val container = @Composable {
        if (foldDef.foldOrientation == FoldingFeature.Orientation.VERTICAL) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(1.0F)
                ) {
                    body()
                }
                hinge()
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(foldDef.widthRightOrBottom)
                ) {
                    secondaryBody()
                }
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.0F)
                ) {
                    body()
                }
                hinge()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(foldDef.heightRightOrBottom)
                ) {
                    secondaryBody()
                }
            }
        }
    }
    container()
}
