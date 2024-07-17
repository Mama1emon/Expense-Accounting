package presentation.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.ImmutableSet

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
@Composable
inline fun <reified T : Any> SelectAlert(
    title: String,
    select: T,
    items: ImmutableSet<T>,
    crossinline onSelect: (T) -> Unit,
    noinline onDismissRequest: () -> Unit,
) {
    var selectedItem by mutableStateOf(value = select)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onSelect(selectedItem)
                    onDismissRequest()
                }
            ) {
                Text(text = "OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = "Cancel")
            }
        },
        title = { Text(text = title) },
        text = {
            Column {
                items.forEach {
                    key(it) {
                        Item(
                            state = it,
                            isSelected = it == selectedItem,
                            onClick = { selectedItem = it }
                        )
                    }
                }
            }
        },
    )
}

@Composable
inline fun <reified T : Any> Item(
    state: T,
    isSelected: Boolean,
    noinline onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = isSelected, onClick = { })

        Text(
            text = if (state is String) {
                state
            } else {
                state::class.simpleName.toString()
            }
        )
    }
}