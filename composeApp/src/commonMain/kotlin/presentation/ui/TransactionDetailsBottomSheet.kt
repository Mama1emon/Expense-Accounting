package presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.AttachMoney
import androidx.compose.material.icons.outlined.CurrencyRuble
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.EuroSymbol
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import domain.ExpenseCategory
import domain.appcurrency.AppCurrency
import presentation.state.MainScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsBottomSheet(
    state: MainScreenState.TransactionDetailsState,
    currency: AppCurrency,
    transaction: MainScreenState.TransactionItemState.Transaction?,
    onDismissRequest: () -> Unit,
) {
    var currency by remember { mutableStateOf(value = transaction?.primaryCurrency ?: currency) }
    var category: ExpenseCategory? by remember { mutableStateOf(value = transaction?.category) }

    var isCategoryMenuExpanded by remember { mutableStateOf(value = false) }
    var isCurrencyMenuExpanded by remember { mutableStateOf(value = false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
    ) {
        val focusManager = LocalFocusManager.current

        var name by remember { mutableStateOf(transaction?.name.orEmpty()) }
        var amount by remember { mutableStateOf(transaction?.primaryAmount?.toString().orEmpty()) }

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Transaction details",
                    style = MaterialTheme.typography.titleLarge
                )

                if (transaction != null) {
                    IconButton(
                        onClick = {
                            state.onDeleteClick(transaction.id)
                            onDismissRequest()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Delete,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Name") },
                placeholder = { Text(text = "Restaurant") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(focusDirection = FocusDirection.Down)
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = amount,
                onValueChange = { amount = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text(text = "Amount") },
                placeholder = { Text(text = "1") },
                trailingIcon = {
                    IconButton(onClick = { isCurrencyMenuExpanded = true }) {
                        AnimatedContent(currency) {
                            Icon(
                                imageVector = when (it) {
                                    AppCurrency.Dollar -> Icons.Outlined.AttachMoney
                                    AppCurrency.Euro -> Icons.Outlined.EuroSymbol
                                    AppCurrency.Ruble -> Icons.Outlined.CurrencyRuble
                                    AppCurrency.IndonesianRupiah -> Icons.Outlined.AccountBalanceWallet
                                },
                                contentDescription = null
                            )
                        }
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    autoCorrect = false,
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Next,
                ),
                keyboardActions = KeyboardActions {
                    focusManager.moveFocus(focusDirection = FocusDirection.Down)
                    isCategoryMenuExpanded = true
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = category?.let { it::class.simpleName }.orEmpty(),
                onValueChange = { isCategoryMenuExpanded = true },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                label = { Text(text = "Category") },
                trailingIcon = {
                    IconButton(
                        onClick = { isCategoryMenuExpanded = !isCategoryMenuExpanded }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = null
                        )
                    }
                },
                singleLine = true,
            )

            Spacer(modifier = Modifier.height(28.dp))

            FilledTonalButton(
                onClick = {
                    onDismissRequest()

                    if (transaction != null) {
                        state.onChangeTransactionClick(
                            transaction.copy(
                                name = name,
                                category = category!!,
                                primaryAmount = amount.toDouble(),
                                primaryCurrency = currency
                            )
                        )
                    } else {
                        state.onAddTransactionClick(name, category!!, amount, currency)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = name.isNotEmpty() && amount.isNotEmpty() && category != null
            ) {
                Text(text = if (transaction != null) "Save" else "Add")
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    SelectMenu(
        isExpanded = isCategoryMenuExpanded,
        items = state.availableCategories,
        onSelect = {
            category = it
            isCategoryMenuExpanded = false
        },
        onDismissRequest = { isCategoryMenuExpanded = false },
    )

    SelectMenu(
        isExpanded = isCurrencyMenuExpanded,
        items = state.availableCurrencies,
        onSelect = {
            currency = it
            isCurrencyMenuExpanded = false
        },
        onDismissRequest = { isCurrencyMenuExpanded = false },
    )
}