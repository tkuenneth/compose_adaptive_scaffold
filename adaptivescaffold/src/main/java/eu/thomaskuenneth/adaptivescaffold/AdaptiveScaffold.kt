package eu.thomaskuenneth.adaptivescaffold

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionContext
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.core.layout.WindowWidthSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowMetricsCalculator
import kotlinx.coroutines.launch

data class NavigationDestination(
    @DrawableRes val icon: Int,
    @StringRes val label: Int,
    val enabled: Boolean = true,
    val alwaysShowLabel: Boolean = true,

    val topBar: @Composable () -> Unit = {},

    val body: @Composable () -> Unit = {},
    val smallBody: @Composable () -> Unit = {},
    val secondaryBody: @Composable () -> Unit = {},
    val smallSecondaryBody: (@Composable () -> Unit)? = null,

    val overlay: (@Composable BoxScope.() -> Unit)? = null
)

val LocalFoldDef = compositionLocalOf { FoldDef() }

fun ComponentActivity.setContentRepeatOnLifecycleStarted(
    enableEdgeToEdge: Boolean = false,
    parent: CompositionContext? = null,
    content: @Composable () -> Unit
) {
    if (enableEdgeToEdge) enableEdgeToEdge()
    lifecycleScope.launch {
        lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            setContent(
                parent = parent,
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity.AdaptiveScaffold(
    useDrawer: Boolean = false,
    startDestination: NavigationDestination,
    otherDestinations: List<NavigationDestination> = emptyList(),
    onDestinationChanged: (NavigationDestination) -> Unit = {},
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit = {},
) {
    AdaptiveScaffold(
        useDrawer = useDrawer,
        destinations = listOf(startDestination).plus(otherDestinations),
        onDestinationChanged = onDestinationChanged,
        topBar = topBar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity.AdaptiveScaffold(
    useDrawer: Boolean = false,
    destinations: List<NavigationDestination>,
    onDestinationChanged: (NavigationDestination) -> Unit = {},
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit = {},
) {
    var index by rememberSaveable(destinations) {
        onDestinationChanged(destinations[0])
        mutableIntStateOf(0)
    }
    val currentDestination = destinations[index]
    AdaptiveScaffold(
        useDrawer = useDrawer,
        index = index,
        onSelectedIndexChange = {
            index = it
            onDestinationChanged(destinations[index])
        },
        destinations = destinations,
        topBar = topBar,
        body = currentDestination.body,
        secondaryBody = currentDestination.secondaryBody,
        smallBody = currentDestination.smallBody,
        smallSecondaryBody = currentDestination.smallSecondaryBody,
        overlay = currentDestination.overlay
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Activity.AdaptiveScaffold(
    useDrawer: Boolean = false,
    index: Int = -1,
    onSelectedIndexChange: (Int) -> Unit = {},
    destinations: List<NavigationDestination> = emptyList(),
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    topBar: @Composable (scrollBehavior: TopAppBarScrollBehavior) -> Unit = {},
    body: @Composable () -> Unit,
    smallBody: @Composable () -> Unit,
    secondaryBody: @Composable () -> Unit,
    smallSecondaryBody: (@Composable () -> Unit)? = null,
    overlay: (@Composable BoxScope.() -> Unit)? = null
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
    var bottomBarHeight by remember { mutableStateOf(0.dp) }
    val localDensity = LocalDensity.current
    CompositionLocalProvider(LocalFoldDef provides foldDef) {
        Scaffold(
            modifier = Modifier
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                topBar(scrollBehavior)
            },
            bottomBar = {
                Box(modifier = Modifier.onGloballyPositioned {
                    bottomBarHeight = with(localDensity) {
                        it.size.height.toDp()
                    }
                }) {
                    AdaptiveScaffoldBottomBar(
                        hasBottomBar = hasBottomBar,
                        index = index,
                        onSelectedIndexChange = onSelectedIndexChange,
                        destinations = destinations
                    )
                }
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
                smallSecondaryBody = smallSecondaryBody,
                overlay = overlay,
                bottomBarHeight = bottomBarHeight
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
                        label = { Text(text = label) }
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
                        label = { Text(text = label) }
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
            modifier = Modifier.consumeWindowInsets(WindowInsets.safeContent),
            drawerContent = {
                PermanentDrawerSheet {
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
    smallSecondaryBody: @Composable (() -> Unit)?,
    overlay: @Composable (BoxScope.() -> Unit)? = null,
    bottomBarHeight: Dp
) {
    val content: @Composable () -> Unit = {
        Row {
            AdaptiveScaffoldNavigationRail(
                hasNavigationRail = hasNavigationRail,
                index = index,
                onSelectedIndexChange = onSelectedIndexChange,
                destinations = destinations,
            )
            Box {
                if (
                    foldDef.windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT &&
                    foldDef.orientation == FoldingFeature.Orientation.HORIZONTAL &&
                    (foldDef.state != FoldingFeature.State.HALF_OPENED)
                ) {
                    TwoPaneScreen(
                        firstPane = smallBody,
                        secondPane = smallSecondaryBody
                    )
                } else if (foldDef.hasFold) {
                    FoldableScreen(
                        foldDef = foldDef,
                        body = body,
                        secondaryBody = secondaryBody,
                        bottomBarHeight = bottomBarHeight
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
                if (overlay != null) {
                    BoxWithConstraints {
                        overlay()
                    }
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
    secondPane: (@Composable () -> Unit)?
) {
    if (secondPane == null) {
        Box(modifier = Modifier.fillMaxSize()) { firstPane() }
    } else {
        Row(
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5F)
            ) {
                firstPane()
            }
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5F)
            ) {
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
    bottomBarHeight: Dp,
) {
    val density = LocalDensity.current
    var y by remember { mutableFloatStateOf(0F) }
    val hinge = @Composable {
        Spacer(
            modifier = Modifier
                .width(foldDef.foldWidth)
                .height(foldDef.foldHeight)
        )
    }
    val container = @Composable {
        if (foldDef.orientation == FoldingFeature.Orientation.VERTICAL) {
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
            Column(modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    y = it.positionInWindow().y
                }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(foldDef.heightLeftOrTop - with(density) { y.toDp() })
                ) {
                    body()
                }
                hinge()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(foldDef.heightRightOrBottom - bottomBarHeight)
                ) {
                    secondaryBody()
                }
            }
        }
    }
    container()
}
