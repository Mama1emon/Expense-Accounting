package presentation.ui

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.window.PopupProperties
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun SelectMenu(
    isExpanded: Boolean,
    items: ImmutableSet<String>,
    onSelect: (String) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
        content = {
            items.forEach { item ->
                key(item) {
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = { onSelect(item) }
                    )
                }
            }
        }
    )
}