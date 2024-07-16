package presentation.ui

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.window.PopupProperties
import kotlinx.collections.immutable.ImmutableSet

@Composable
inline fun <reified T : Any> SelectMenu(
    isExpanded: Boolean,
    items: ImmutableSet<T>,
    crossinline onSelect: (T) -> Unit,
    noinline onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
        content = {
            items.forEach { item ->
                key(item) {
                    DropdownMenuItem(
                        text = { Text(text = item::class.simpleName.orEmpty()) },
                        onClick = { onSelect(item) }
                    )
                }
            }
        }
    )
}