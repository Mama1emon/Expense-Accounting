package utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.unit.Dp

/**
 * @author Andrew Khokhlov on 18/07/2024
 */
/**
 * @author Andrew Khokhlov on 18/07/2024
 */
@OptIn(ExperimentalComposeUiApi::class)
@Composable
actual fun getScreenWidth(): Dp {
    val density = LocalDensity.current

    return with(density) { LocalWindowInfo.current.containerSize.width.toDp() }
}