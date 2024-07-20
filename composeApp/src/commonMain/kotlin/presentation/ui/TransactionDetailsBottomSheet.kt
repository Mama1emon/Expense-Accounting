package presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import domain.appcurrency.AppCurrency
import domain.selectedmonth.SelectedMonth
import kotlinx.datetime.*
import kotlinx.datetime.format.*
import network.chaintech.kmp_date_time_picker.utils.now
import presentation.converters.AppCurrencyUtils
import presentation.state.MainScreenState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDetailsBottomSheet(
    state: MainScreenState.TransactionDetailsState,
    params: MainScreenState.BaseParams,
    transaction: MainScreenState.TransactionItemState.Transaction?,
    onSnackbarShow: (SnackbarVisuals, (SnackbarResult) -> Unit) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var currency by remember {
        mutableStateOf(value = transaction?.primaryCurrency ?: params.appCurrency)
    }
    var category: String? by remember { mutableStateOf(value = transaction?.category) }

    var date: Long? by remember {
        mutableStateOf(value = transaction?.timestamp ?: Clock.System.now().toEpochMilliseconds())
    }

    var isCategoryMenuExpanded by remember { mutableStateOf(value = false) }
    var isCurrencyMenuExpanded by remember { mutableStateOf(value = false) }
    var isDatePickerVisible by remember { mutableStateOf(value = false) }

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        sheetMaxWidth = Dp.Unspecified,
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
                                imageVector = when (AppCurrencyUtils.valueOf(it)) {
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

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedCard(
                modifier = Modifier
                    .clickable(onClick = { isCategoryMenuExpanded = !isCategoryMenuExpanded }),
                shape = MaterialTheme.shapes.extraSmall,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(category) {
                        Text(
                            text = it ?: "Category",
                            color = it?.let { MaterialTheme.colorScheme.onSurface }
                                ?: MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.ArrowDropDown,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedCard(
                modifier = Modifier
                    .clickable(onClick = { isDatePickerVisible = true }),
                shape = MaterialTheme.shapes.extraSmall,
                colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 56.dp)
                        .padding(start = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AnimatedContent(date) {
                        Text(
                            text = it?.let {
                                Instant
                                    .fromEpochMilliseconds(it)
                                    .toLocalDateTime(TimeZone.currentSystemDefault())
                                    .format(
                                        LocalDateTime.Format {
                                            monthName(MonthNames.ENGLISH_ABBREVIATED)
                                            char(' ')
                                            dayOfMonth(padding = Padding.NONE)
                                        }
                                    )
                            } ?: "Date",
                            color = it?.let { MaterialTheme.colorScheme.onSurface }
                                ?: MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }

                    Box(modifier = Modifier.size(48.dp), contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

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
                                primaryCurrency = currency,
                                timestamp = date!!
                            )
                        )
                    } else {
                        state.onAddTransactionClick(name, category!!, amount, currency, date!!)
                    }

                    val inputDate = Instant.fromEpochMilliseconds(date!!)
                        .toLocalDateTime(TimeZone.currentSystemDefault())
                        .let { SelectedMonth(it.monthNumber).fullName }

                    if (params.selectedMonth != inputDate) {
                        onSnackbarShow(
                            object : SnackbarVisuals {
                                override val actionLabel = "See"
                                override val duration = SnackbarDuration.Long
                                override val message = "Transaction was added to another month"
                                override val withDismissAction: Boolean = false
                            }
                        ) {
                            if (it == SnackbarResult.ActionPerformed) {
                                state.onChangeMonthClick(inputDate)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = name.isNotEmpty() && amount.isNotEmpty() && category != null && date != null
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
        items = params.availableAppCurrencies,
        onSelect = {
            currency = it
            isCurrencyMenuExpanded = false
        },
        onDismissRequest = { isCurrencyMenuExpanded = false },
    )

    if (isDatePickerVisible) {
        EaDatePickerAlert(
            maxDate = LocalDate.now(),
            onDismissRequest = { isDatePickerVisible = false },
            onConfirm = {
                date = it.atTime(0, 0)
                    .toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
            }
        )
    }
}