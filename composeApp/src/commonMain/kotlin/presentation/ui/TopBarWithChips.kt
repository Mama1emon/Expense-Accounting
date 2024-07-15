package presentation.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CurrencyExchange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import domain.appcurrency.AppCurrency
import presentation.state.MainScreenState

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
@Composable
fun TopBarWithChips(state: MainScreenState.TopBarState) {
    var isDialogExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .padding(start = 16.dp, end = 12.dp)
            .padding(vertical = 8.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Expense Accounting",
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleLarge
            )

            IconButton(onClick = { isDialogExpanded = true }) {
                Icon(
                    imageVector = Icons.Rounded.CurrencyExchange,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        TransactionFilters(
            categories = state.filterCategories,
            onCategoryFilterClick = state.onFilterByCategoryClick,
        )
    }

    if (isDialogExpanded) {
        var appCurrency by mutableStateOf(value = state.selectedAppCurrency)

        AlertDialog(
            onDismissRequest = { isDialogExpanded = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        state.onChangeAppCurrencyClick(appCurrency)
                        isDialogExpanded = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isDialogExpanded = false }) {
                    Text(text = "Cancel")
                }
            },
            title = { Text(text = "Select currency") },
            text = {
                Column {
                    state.availableAppCurrencies.forEach {
                        key(it) {
                            AppCurrencyItem(
                                state = it,
                                isSelected = it == appCurrency,
                                onClick = { appCurrency = it }
                            )
                        }
                    }
                }
            },
        )
    }
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