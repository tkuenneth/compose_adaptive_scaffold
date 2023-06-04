package eu.thomaskuenneth.adaptivescaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.window.core.layout.WindowSizeClass
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowLayoutInfo
import androidx.window.layout.WindowMetrics

data class FoldDef(
    val hasFold: Boolean,
    val foldOrientation: FoldingFeature.Orientation?,
    val occlusionType: FoldingFeature.OcclusionType,
    val isFoldSeparating: Boolean,
    val foldWidth: Dp,
    val foldHeight: Dp,
    val widthLeftOrTop: Dp,
    val heightLeftOrTop: Dp,
    val widthRightOrBottom: Dp,
    val heightRightOrBottom: Dp,
    val isPortrait: Boolean,
    val windowSizeClass: WindowSizeClass,
)

@Composable
fun createFoldDef(
    layoutInfo: WindowLayoutInfo?,
    windowMetrics: WindowMetrics
): FoldDef {
    var foldOrientation: FoldingFeature.Orientation? = null
    var isFoldSeparating = false
    var foldOcclusionType = FoldingFeature.OcclusionType.NONE
    var widthLeftOrTop = 0
    var heightLeftOrTop = 0
    var widthRightOrBottom = 0
    var heightRightOrBottom = 0
    var foldWidth = 0
    var foldHeight = 0
    layoutInfo?.displayFeatures?.forEach { displayFeature ->
        (displayFeature as FoldingFeature).run {
            foldOrientation = orientation
            isFoldSeparating = isSeparating
            foldOcclusionType = occlusionType
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
            foldWidth = bounds.width()
            foldHeight = bounds.height()
        }
    }
    return with(LocalDensity.current) {
        FoldDef(
            foldOrientation = foldOrientation,
            isFoldSeparating = isFoldSeparating,
            occlusionType = foldOcclusionType,
            widthLeftOrTop = widthLeftOrTop.toDp(),
            heightLeftOrTop = heightLeftOrTop.toDp(),
            widthRightOrBottom = widthRightOrBottom.toDp(),
            heightRightOrBottom = heightRightOrBottom.toDp(),
            foldWidth = foldWidth.toDp(),
            foldHeight = foldHeight.toDp(),
            isPortrait = windowWidthDp(windowMetrics) / windowHeightDp(windowMetrics) <= 1F,
            windowSizeClass = WindowSizeClass.compute(
                dpWidth = windowWidthDp(windowMetrics = windowMetrics).value,
                dpHeight = windowHeightDp(windowMetrics = windowMetrics).value
            ),
            hasFold = foldOrientation != null
        )
    }
}

@Composable
fun windowWidthDp(windowMetrics: WindowMetrics): Dp = with(LocalDensity.current) {
    windowMetrics.bounds.width().toDp()
}

@Composable
fun windowHeightDp(windowMetrics: WindowMetrics): Dp = with(LocalDensity.current) {
    windowMetrics.bounds.height().toDp()
}
