package presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AddTransactionUseCase
import domain.Amount
import domain.CalculateExpensesUseCase
import domain.ConvertCurrencyUseCase
import domain.ExpenseCategory
import domain.GetAllTransactionsUseCase
import domain.Transaction
import domain.appcurrency.AppCurrency
import domain.appcurrency.ChangeAppCurrencyUseCase
import domain.appcurrency.GetAppCurrencyUseCase
import domain.transactions.ChangeTransactionUseCase
import domain.transactions.DeleteTransactionUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import presentation.converters.TransactionStateConverter
import presentation.formatters.AmountFormatter
import presentation.formatters.DateFormatter
import presentation.state.MainScreenState

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
class MainViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getCalculateExpensesUseCase: CalculateExpensesUseCase,
    private val getAppCurrencyUseCase: GetAppCurrencyUseCase,
    private val changeAppCurrencyUseCase: ChangeAppCurrencyUseCase,
    private val convertCurrencyUseCase: ConvertCurrencyUseCase,
    private val changeTransactionUseCase: ChangeTransactionUseCase,
    private val deleteTransactionUseCase: DeleteTransactionUseCase,
) : ViewModel() {

    private val groupBy = MutableStateFlow(value = MainScreenState.Group.Date)

    private val _uiState = MutableStateFlow(value = getInitState())
    val uiState = _uiState.asStateFlow()

    private val filterCategory = MutableStateFlow<ExpenseCategory?>(value = null)

    init {
        subscribeOnTransactionsUpdate()
    }

    private fun getInitState(): MainScreenState {
        val availableCurrencies = persistentSetOf(
            AppCurrency.Dollar,
            AppCurrency.Euro,
            AppCurrency.Ruble,
            AppCurrency.IndonesianRupiah,
        )

        val availableCategories = persistentSetOf(
            ExpenseCategory.Housing,
            ExpenseCategory.Transport,
            ExpenseCategory.Cellular,
            ExpenseCategory.Sport,
            ExpenseCategory.HealthAndCare,
            ExpenseCategory.Household,
            ExpenseCategory.Clothing,
            ExpenseCategory.Entertainment,
            ExpenseCategory.Gifts,
            ExpenseCategory.Documents,
            ExpenseCategory.Travel,
            ExpenseCategory.FoodDelivery,
            ExpenseCategory.Food,
            ExpenseCategory.Other,
        )

        return MainScreenState(
            appCurrency = AppCurrency.Dollar,
            groupBy = groupBy.value,
            topBarState = MainScreenState.TopBarState(
                availableGroups = MainScreenState.Group.entries.toImmutableSet(),
                availableAppCurrencies = availableCurrencies,
                filterCategories = persistentSetOf(),
                onChangeGroupClick = ::changeGrouping,
                onChangeAppCurrencyClick = ::changeAppCurrency,
                onFilterByCategoryClick = ::filterByCategory,
            ),
            transactions = persistentListOf(),
            availableCategories = availableCategories,
            transactionDetailsState = MainScreenState.TransactionDetailsState(
                availableCurrencies = availableCurrencies,
                availableCategories = availableCategories,
                onAddTransactionClick = ::addTransaction,
                onChangeTransactionClick = ::changeTransaction,
                onDeleteClick = ::deleteTransaction
            ),
            summaryState = MainScreenState.SummaryState(
                total = "0",
                totalCategories = persistentMapOf()
            ),
        )
    }

    private fun subscribeOnTransactionsUpdate() {
        combine(
            flow = getAllTransactionsUseCase(),
            flow2 = filterCategory,
            flow3 = getAppCurrencyUseCase(),
            flow4 = groupBy,
        ) { transactions, filterCategory, appCurrency, groupBy ->
            if (filterCategory == null) {
                transactions
            } else {
                transactions.filter { it.expenseCategory == filterCategory }
            } to filterCategory

            Triple(transactions, filterCategory to groupBy, appCurrency)
        }
            .onEach {
                val (transactions, filterAndCategory, appCurrency) = it
                val (filterCategory, groupBy) = filterAndCategory

                _uiState.value = _uiState.value.copy(
                    appCurrency = appCurrency,
                    groupBy = groupBy,
                    topBarState = uiState.value.topBarState.copy(
                        // TODO: empty list?
                        filterCategories = transactions
                            .map(Transaction::expenseCategory)
                            .toImmutableSet(),
                    ),
                    transactions = createTransactionItems(transactions, appCurrency, groupBy),
                    summaryState = MainScreenState.SummaryState(
                        total = calculateTotalByCategory(filterCategory, appCurrency),
                        totalCategories = if (filterCategory == null) {
                            transactions
                                .map(Transaction::expenseCategory)
                                .toSet()
                                .associateWith {
                                    calculateTotalByCategory(
                                        category = it,
                                        appCurrency = appCurrency
                                    )
                                }
                                .toImmutableMap()
                        } else {
                            persistentMapOf()
                        }
                    )
                )
            }
            .launchIn(viewModelScope)
    }

    private suspend fun createTransactionItems(
        transactions: List<Transaction>,
        appCurrency: AppCurrency,
        groupBy: MainScreenState.Group,
    ): ImmutableList<MainScreenState.TransactionItemState> {
        return when (groupBy) {
            MainScreenState.Group.Date -> {
                buildList {
                    transactions
                        .sortedByDescending(Transaction::timestamp)
                        .groupBy {
                            DateFormatter.format(timestamp = it.timestamp)
                        }
                        .forEach {
                            add(
                                MainScreenState.TransactionItemState.Title(value = it.key)
                            )

                            addAll(
                                it.value.map {
                                    TransactionStateConverter(
                                        amount = convertCurrencyUseCase(it.amount, appCurrency)
                                    ).convert(it)
                                }
                            )
                        }
                }
                    .toImmutableList()
            }

            else -> {
                transactions
                    .reversed()
                    .map { transaction ->
                        TransactionStateConverter(
                            amount = convertCurrencyUseCase(transaction.amount, appCurrency)
                        ).convert(transaction)
                    }
                    .toImmutableList()
            }
        }
    }

    private fun changeGrouping(group: MainScreenState.Group) {
        viewModelScope.launch {
            groupBy.value = group
        }
    }

    private suspend fun calculateTotalByCategory(
        category: ExpenseCategory?,
        appCurrency: AppCurrency,
    ): String {
        val total = getCalculateExpensesUseCase(
            startTimestamp = 0, // TODO
            endTimestamp = Clock.System.now().toEpochMilliseconds(),
            category = category,
            appCurrency = appCurrency,
        )

        return AmountFormatter.formatAmount(total, appCurrency)
    }

    private fun changeAppCurrency(appCurrency: AppCurrency) {
        viewModelScope.launch {
            changeAppCurrencyUseCase(appCurrency)
        }
    }

    private fun addTransaction(
        name: String,
        expenseCategory: ExpenseCategory,
        amount: String,
        appCurrency: AppCurrency
    ) {
        viewModelScope.launch {
            addTransactionUseCase(
                name = name,
                expenseCategory = expenseCategory,
                amount = Amount(
                    value = amount.toDouble(),
                    currency = appCurrency,
                )
            )
        }
    }

    private fun changeTransaction(state: MainScreenState.TransactionItemState.Transaction) {
        viewModelScope.launch {
            changeTransactionUseCase(
                id = state.id,
                name = state.name,
                expenseCategory = state.category,
                amount = Amount(value = state.primaryAmount, currency = state.primaryCurrency)
            )
        }
    }

    private fun deleteTransaction(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }

    private fun filterByCategory(category: ExpenseCategory?) {
        filterCategory.value = category
    }
}