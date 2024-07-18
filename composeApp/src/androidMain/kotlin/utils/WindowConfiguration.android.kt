package utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * @author Andrew Khokhlov on 18/07/2024
 */
/**
 * @author Andrew Khokhlov on 18/07/2024
 */
@Composable
actual fun getScreenWidth(): Dp {
    return LocalConfiguration.current.screenWidthDp.dp
}