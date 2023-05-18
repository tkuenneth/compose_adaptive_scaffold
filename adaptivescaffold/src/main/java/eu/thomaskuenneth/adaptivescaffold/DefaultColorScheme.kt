package eu.thomaskuenneth.adaptivescaffold

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun defaultColorScheme() = with(isSystemInDarkTheme()) {
    val hasDynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    when (this) {
        true -> if (hasDynamicColor) {
            dynamicDarkColorScheme(context)
        } else {
            darkColorScheme()
        }

        false -> if (hasDynamicColor) {
            dynamicLightColorScheme(context)
        } else {
            lightColorScheme()
        }
    }
}
