package presentation.ui

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.window.PopupProperties
import domain.appcurrency.AppCurrency
import kotlinx.collections.immutable.ImmutableSet

@Composable
fun ChooseCurrencyMenu(
    isExpanded: Boolean,
    currencies: ImmutableSet<AppCurrency>,
    onCurrencyClick: (AppCurrency) -> Unit,
    onDismissRequest: () -> Unit,
) {
    DropdownMenu(
        expanded = isExpanded,
        onDismissRequest = onDismissRequest,
        properties = PopupProperties(focusable = true),
        content = {
            currencies.forEach {
                key(it) {
                    DropdownMenuItem(
                        text = {
                            Text(text = it::class.simpleName.orEmpty())
                        },
                        onClick = { onCurrencyClick(it) }
                    )
                }
            }
        }
    )
}