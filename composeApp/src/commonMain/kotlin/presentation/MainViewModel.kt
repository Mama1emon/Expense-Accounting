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
import kotlinx.collections.immutable.persistentSetOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableSet
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import presentation.converters.AppCurrencyUtils
import presentation.converters.ExpenseCategoryUtils
import presentation.converters.TransactionStateConverter
import presentation.converters.name
import presentation.formatters.AmountFormatter
import presentation.formatters.DateFormatter
import presentation.state.MainScreenState
import presentation.state.MainScreenState.Filter

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

    private val filters = MutableStateFlow(value = emptyMap<Filter, String?>())

    init {
        subscribeOnTransactionsUpdate()
    }

    // region Primary state initialization
    private fun getInitState(): MainScreenState {
        return MainScreenState(
            params = createInitBaseParams(),
            topBarState = createInitTopBarState(),
            transactions = persistentListOf(),
            transactionDetailsState = createInitTransactionDetailsState(),
            summaryState = createInitSummaryState(),
        )
    }

    private fun createInitBaseParams(): MainScreenState.BaseParams {
        return MainScreenState.BaseParams(
            appCurrency = AppCurrency.Dollar.name,
            availableAppCurrencies = persistentSetOf(
                AppCurrency.Dollar,
                AppCurrency.Euro,
                AppCurrency.Ruble,
                AppCurrency.IndonesianRupiah,
            )
                .map(AppCurrency::name)
                .toImmutableSet(),
            groupBy = groupBy.value,
        )
    }

    private fun createInitTopBarState(): MainScreenState.TopBarState {
        return MainScreenState.TopBarState(
            filterCategories = persistentSetOf(),
            filterCurrencies = persistentSetOf(),
            onChangeGroupClick = ::changeGrouping,
            onChangeAppCurrencyClick = ::changeAppCurrency,
            onFilterClick = ::changeFilters,
        )
    }

    private fun createInitTransactionDetailsState(): MainScreenState.TransactionDetailsState {
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

        return MainScreenState.TransactionDetailsState(
            availableCategories = availableCategories
                .map(ExpenseCategory::name)
                .toImmutableSet(),
            onAddTransactionClick = ::addTransaction,
            onChangeTransactionClick = ::changeTransaction,
            onDeleteClick = ::deleteTransaction
        )
    }

    private fun createInitSummaryState(): MainScreenState.SummaryState {
        return MainScreenState.SummaryState(
            total = "0",
            totalCategories = persistentListOf()
        )
    }
    // endregion

    private fun subscribeOnTransactionsUpdate() {
        combine(
            flow = getAllTransactionsUseCase(),
            flow2 = filters,
            flow3 = getAppCurrencyUseCase(),
            flow4 = groupBy,
        ) { transactions, filters, appCurrency, groupBy ->
            val filterTransactions = if (filters.all { it.value == null }) {
                transactions
            } else {
                transactions.filter { tx ->
                    filters
                        .filterValues { it != null }
                        .map {
                            val filter = it.value!!
                            when (it.key) {
                                Filter.Category -> {
                                    val filterCategory = filter
                                        .asFilterValue<ExpenseCategory>(Filter.Category)

                                    tx.expenseCategory == filterCategory
                                }

                                Filter.Date -> {
                                    val filterDate = filter
                                        .asFilterValue<LocalDateTime>(Filter.Date)

                                    val txDate = Instant
                                        .fromEpochMilliseconds(tx.timestamp)
                                        .toLocalDateTime(TimeZone.currentSystemDefault())

                                    txDate.date == filterDate.date
                                }

                                Filter.Currency -> {
                                    val filterCurrency = filter
                                        .asFilterValue<AppCurrency>(Filter.Currency)

                                    tx.amount.currency == filterCurrency
                                }
                            }
                        }
                        .all { it }
                }
            }

            Triple(filterTransactions, filters to groupBy, appCurrency)
        }
            .onEach {
                val (transactions, filtersAndGroup, appCurrency) = it
                val (filters, groupBy) = filtersAndGroup

                _uiState.value = _uiState.value.copy(
                    params = _uiState.value.params.copy(
                        appCurrency = appCurrency.name,
                        groupBy = groupBy,
                    ),
                    topBarState = uiState.value.topBarState.copy(
                        filterCategories = transactions
                            .map { it.expenseCategory.name }
                            .toImmutableSet(),
                        filterCurrencies = transactions
                            .map { it.amount.currency.name }
                            .toImmutableSet(),
                    ),
                    transactions = createTransactionItems(
                        transactions = transactions,
                        appCurrency = appCurrency,
                        groupBy = groupBy,
                    ),
                    summaryState = createSummaryState(
                        transactions = transactions,
                        filters = filters,
                        appCurrency = appCurrency
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
        return buildList {
            transactions
                .sortedByDescending(Transaction::timestamp)
                .groupBy { transaction ->
                    when (groupBy) {
                        MainScreenState.Group.Date -> {
                            DateFormatter.format(timestamp = transaction.timestamp)
                        }

                        MainScreenState.Group.Category -> {
                            transaction.expenseCategory.name
                        }

                        MainScreenState.Group.Currency -> {
                            transaction.amount.currency::class.simpleName.toString()
                        }
                    }
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

    private suspend fun createSummaryState(
        transactions: List<Transaction>,
        filters: Map<Filter, String?>,
        appCurrency: AppCurrency,
    ): MainScreenState.SummaryState {
        val filterCategory = filters.getFilterValue<ExpenseCategory>(Filter.Category)
        val filterDate = filters.getFilterValue<LocalDateTime>(Filter.Date)

        return MainScreenState.SummaryState(
            total = calculateTotalByCategory(
                category = filterCategory,
                date = filterDate,
                appCurrency = appCurrency,
            ),
            totalCategories = if (filterCategory == null) {
                transactions
                    .map(Transaction::expenseCategory)
                    .distinct()
            } else {
                listOf(filterCategory)
            }
                .map { expenseCategory ->
                    MainScreenState.SummaryState.TotalCategory(
                        category = expenseCategory.name,
                        total = calculateTotalByCategory(expenseCategory, filterDate, appCurrency)
                    )
                }
                .toImmutableList()
        )
    }

    private fun changeGrouping(group: MainScreenState.Group) {
        viewModelScope.launch {
            groupBy.value = group
        }
    }

    private suspend fun calculateTotalByCategory(
        category: ExpenseCategory?,
        date: LocalDateTime?,
        appCurrency: AppCurrency,
    ): String {
        val total = getCalculateExpensesUseCase(
            startTimestamp = date
                ?.toInstant(TimeZone.currentSystemDefault())
                ?.toEpochMilliseconds()
                ?: 0,
            endTimestamp = date?.let {
                LocalDateTime(
                    year = it.year,
                    month = it.month,
                    dayOfMonth = it.dayOfMonth,
                    hour = 23,
                    minute = 59,
                    second = 59
                )
                    .toInstant(TimeZone.currentSystemDefault())
                    .toEpochMilliseconds()
            }
                ?: Clock.System.now().toEpochMilliseconds(),
            category = category,
            appCurrency = appCurrency,
        )

        return AmountFormatter.formatAmount(total, appCurrency)
    }

    private fun changeAppCurrency(appCurrency: String) {
        viewModelScope.launch {
            changeAppCurrencyUseCase(
                appCurrency = AppCurrencyUtils.valueOf(appCurrency),
            )
        }
    }

    private fun addTransaction(
        name: String,
        expenseCategory: String,
        amount: String,
        appCurrency: String,
    ) {
        viewModelScope.launch {
            addTransactionUseCase(
                name = name,
                expenseCategory = ExpenseCategoryUtils.valueOf(expenseCategory),
                amount = Amount(
                    value = amount.toDouble(),
                    currency = AppCurrencyUtils.valueOf(appCurrency)
                )
            )
        }
    }

    private fun changeTransaction(state: MainScreenState.TransactionItemState.Transaction) {
        viewModelScope.launch {
            changeTransactionUseCase(
                id = state.id,
                name = state.name,
                expenseCategory = ExpenseCategoryUtils.valueOf(state.category),
                amount = Amount(
                    value = state.primaryAmount,
                    currency = AppCurrencyUtils.valueOf(state.primaryCurrency)
                )
            )
        }
    }

    private fun deleteTransaction(id: String) {
        viewModelScope.launch {
            deleteTransactionUseCase(id)
        }
    }

    private fun changeFilters(filter: Filter, value: String?) {
        filters.value = filters.value.toMutableMap().apply {
            this[filter] = value
        }
    }

    private inline fun <reified T> Map<Filter, String?>.getFilterValue(filter: Filter): T? {
        return get(filter)?.asFilterValue(filter)
    }

    private inline fun <reified T> String.asFilterValue(filter: Filter): T {
        return when (filter) {
            Filter.Category -> ExpenseCategoryUtils.valueOf(value = this) as T
            Filter.Currency -> AppCurrencyUtils.valueOf(value = this) as T
            Filter.Date -> {
                Instant
                    .fromEpochMilliseconds(epochMilliseconds = toLong())
                    .toLocalDateTime(TimeZone.currentSystemDefault()) as T
            }
        }
    }
}