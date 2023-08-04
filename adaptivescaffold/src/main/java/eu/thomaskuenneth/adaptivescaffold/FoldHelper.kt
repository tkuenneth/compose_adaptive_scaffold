package eu.thomaskuenneth.adaptivescaffold

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowInsetsCompat
import androidx.window.core.ExperimentalWindowApi
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

// Surface Duo and Duo 2 width/height (depending on orientation) with hinge
private val widthOrHeight = listOf(2784, 2754)

@OptIn(ExperimentalWindowApi::class)
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
            val navigationBottomOrRight = with(
                windowMetrics.getWindowInsets()
                    .getInsets(WindowInsetsCompat.Type.navigationBars())
            ) {
                if (orientation == FoldingFeature.Orientation.HORIZONTAL)
                    bottom
                else
                    right
            }
            var foldWidth = bounds.width()
            var foldHeight = bounds.height()
            val foldAdjusted = isSurfaceDuo && (foldWidth == 0 || foldHeight == 0)
            if (orientation == FoldingFeature.Orientation.VERTICAL) {
                if (widthOrHeight.contains(windowMetrics.bounds.width())) {
                    if (foldAdjusted) foldWidth = if (foldHeight == 1800) 84 else 66
                }
                widthLeftOrTop = if (foldAdjusted)
                    (windowMetrics.bounds.width() - foldWidth) / 2
                else
                    bounds.left
                heightLeftOrTop = windowMetrics.bounds.height()
                widthRightOrBottom = if (foldAdjusted)
                    (windowMetrics.bounds.width() - foldWidth) / 2
                else
                    windowMetrics.bounds.width() - bounds.right
                heightRightOrBottom = heightLeftOrTop - navigationBottomOrRight
            } else if (orientation == FoldingFeature.Orientation.HORIZONTAL) {
                if (widthOrHeight.contains(windowMetrics.bounds.height())) {
                    if (foldAdjusted) foldHeight = if (foldWidth == 1800) 84 else 66
                }
                widthLeftOrTop = windowMetrics.bounds.width()
                heightLeftOrTop = bounds.top
                widthRightOrBottom = windowMetrics.bounds.width()
                heightRightOrBottom = (if (foldAdjusted)
                    (windowMetrics.bounds.height() - foldHeight) / 2
                else
                    windowMetrics.bounds.height() - bounds.bottom) - navigationBottomOrRight
            }
            return with(LocalDensity.current) {
                FoldDef(
                    hasFold = true,
                    orientation = orientation,
                    occlusionType = occlusionType,
                    isSeparating = isSeparating,
                    state = state,
                    foldWidth = foldWidth.toDp(),
                    foldHeight = foldHeight.toDp(),
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

private val isSurfaceDuo: Boolean =
    "${Build.MANUFACTURER} ${Build.MODEL}".contains("Microsoft Surface Duo")
