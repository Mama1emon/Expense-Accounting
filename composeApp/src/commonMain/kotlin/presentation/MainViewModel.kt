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
import presentation.converters.AppCurrencyUtils
import presentation.converters.ExpenseCategoryUtils
import presentation.converters.TransactionStateConverter
import presentation.converters.name
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
            onChangeGroupClick = ::changeGrouping,
            onChangeAppCurrencyClick = ::changeAppCurrency,
            onFilterByCategoryClick = ::filterByCategory,
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
                    params = _uiState.value.params.copy(
                        appCurrency = appCurrency.name,
                        groupBy = groupBy,
                    ),
                    topBarState = uiState.value.topBarState.copy(
                        // TODO: empty list?
                        filterCategories = transactions
                            .map(Transaction::expenseCategory)
                            .map(ExpenseCategory::name)
                            .toImmutableSet(),
                    ),
                    transactions = createTransactionItems(
                        transactions = transactions,
                        appCurrency = appCurrency,
                        groupBy = groupBy,
                        filterCategory = filterCategory
                    ),
                    summaryState = createSummaryState(transactions, filterCategory, appCurrency)
                )
            }
            .launchIn(viewModelScope)
    }

    private suspend fun createTransactionItems(
        transactions: List<Transaction>,
        appCurrency: AppCurrency,
        groupBy: MainScreenState.Group,
        filterCategory: ExpenseCategory?,
    ): ImmutableList<MainScreenState.TransactionItemState> {
        return buildList {
            transactions
                .filter {
                    if (filterCategory == null) {
                        true
                    } else {
                        it.expenseCategory == filterCategory
                    }
                }
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
        filterCategory: ExpenseCategory?,
        appCurrency: AppCurrency,
    ): MainScreenState.SummaryState {
        return MainScreenState.SummaryState(
            total = calculateTotalByCategory(filterCategory, appCurrency),
            totalCategories = if (filterCategory == null) {
                transactions.distinctBy(Transaction::expenseCategory)
            } else {
                transactions.filter { it.expenseCategory == filterCategory }
            }
                .map(Transaction::expenseCategory)
                .map { expenseCategory ->
                    MainScreenState.SummaryState.TotalCategory(
                        category = expenseCategory.name,
                        total = calculateTotalByCategory(expenseCategory, appCurrency)
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

    private fun filterByCategory(category: String?) {
        filterCategory.value = category?.let(ExpenseCategoryUtils::valueOf)
    }
}