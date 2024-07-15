package presentation.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import domain.appcurrency.AppCurrency
import kotlinx.collections.immutable.ImmutableSet

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
@Composable
fun SelectCurrencyAlert(
    selectedCurrency: AppCurrency,
    availableCurrencies: ImmutableSet<AppCurrency>,
    onSelect: (AppCurrency) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var currency by mutableStateOf(value = selectedCurrency)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        confirmButton = {
            TextButton(
                onClick = {
                    onSelect(currency)
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
        title = { Text(text = "Select currency") },
        text = {
            Column {
                availableCurrencies.forEach {
                    key(it) {
                        AppCurrencyItem(
                            state = it,
                            isSelected = it == currency,
                            onClick = { currency = it }
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun AppCurrencyItem(
    state: AppCurrency,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = isSelected, onClick = onClick)

        Text(text = state::class.simpleName.toString())
    }
}