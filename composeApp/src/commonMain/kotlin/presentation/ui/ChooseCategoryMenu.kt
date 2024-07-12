package presentation.ui

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.window.PopupProperties
import domain.ExpenseCategory
import kotlinx.collections.immutable.ImmutableList

@Composable
fun ChooseCategoryMenu(
    isExpanded: Boolean,
    categories: ImmutableList<ExpenseCategory>,
    onCategoryClick: (ExpenseCategory) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
        content = {
            categories.forEach {
                key(it) {
                    DropdownMenuItem(
                        text = {
                            Text(text = it::class.simpleName.orEmpty())
                        },
                        onClick = { onCategoryClick(it) }
                    )
                }
            }
        }
    )
}