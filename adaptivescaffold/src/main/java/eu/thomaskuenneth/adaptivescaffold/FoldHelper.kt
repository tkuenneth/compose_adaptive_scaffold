package eu.thomaskuenneth.adaptivescaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics

data class FoldDef(
    val hasFold: Boolean = false,
    val orientation: FoldingFeature.Orientation? = null,
    val occlusionType: FoldingFeature.OcclusionType = FoldingFeature.OcclusionType.NONE,
    val isSeparating: Boolean = false,
    val state: FoldingFeature.State = FoldingFeature.State.FLAT,
    val foldWidth: Dp = 0.dp,
    val foldHeight: Dp = 0.dp,
    val widthLeftOrTop: Dp = 0.dp,
    val heightLeftOrTop: Dp = 0.dp,
    val widthRightOrBottom: Dp = 0.dp,
    val heightRightOrBottom: Dp = 0.dp,
    val isPortrait: Boolean = false,
    val windowSizeClass: WindowSizeClass = WindowSizeClass.compute(
        dpWidth = 1F,
        dpHeight = 1F
    ),
    val bounds: android.graphics.Rect = android.graphics.Rect(0, 0, 0, 0),
) {
    companion object {
        val EMPTY = FoldDef()
    }
}

@Composable
fun createFoldDef(
    layoutInfo: WindowLayoutInfo?,
    windowMetrics: WindowMetrics
): FoldDef {
    var widthLeftOrTop = 0
    var heightLeftOrTop = 0
    var widthRightOrBottom = 0
    var heightRightOrBottom = 0
    layoutInfo?.displayFeatures?.forEach { displayFeature ->
        (displayFeature as FoldingFeature).run {
            if (orientation == FoldingFeature.Orientation.VERTICAL) {
                widthLeftOrTop = bounds.left
                heightLeftOrTop = windowMetrics.bounds.height()
                widthRightOrBottom = windowMetrics.bounds.width() - bounds.right
                heightRightOrBottom = heightLeftOrTop
            } else if (orientation == FoldingFeature.Orientation.HORIZONTAL) {
                widthLeftOrTop = windowMetrics.bounds.width()
                heightLeftOrTop = bounds.top
                widthRightOrBottom = windowMetrics.bounds.width()
                heightRightOrBottom = windowMetrics.bounds.height() - bounds.bottom
            }
            return with(LocalDensity.current) {
                FoldDef(
                    hasFold = true,
                    orientation = orientation,
                    occlusionType = occlusionType,
                    isSeparating = isSeparating,
                    state = state,
                    foldWidth = bounds.width().toDp(),
                    foldHeight = bounds.height().toDp(),
                    widthLeftOrTop = widthLeftOrTop.toDp(),
                    heightLeftOrTop = heightLeftOrTop.toDp(),
                    widthRightOrBottom = widthRightOrBottom.toDp(),
                    heightRightOrBottom = heightRightOrBottom.toDp(),
                    isPortrait = windowWidthDp(windowMetrics) / windowHeightDp(windowMetrics) <= 1F,
                    windowSizeClass = WindowSizeClass.compute(
                        dpWidth = windowWidthDp(windowMetrics = windowMetrics).value,
                        dpHeight = windowHeightDp(windowMetrics = windowMetrics).value
                    ),
                    bounds = bounds,
                )
            }
        }
    }
    return FoldDef.EMPTY
}

@Composable
fun windowWidthDp(windowMetrics: WindowMetrics): Dp = with(LocalDensity.current) {
    windowMetrics.bounds.width().toDp()
}

@Composable
fun windowHeightDp(windowMetrics: WindowMetrics): Dp = with(LocalDensity.current) {
    windowMetrics.bounds.height().toDp()
}
