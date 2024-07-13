package presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AddTransactionUseCase
import domain.CalculateExpensesUseCase
import domain.ExpenseCategory
import domain.GetAllTransactionsUseCase
import domain.Transaction
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.collections.immutable.toImmutableMap
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import presentation.state.MainScreenState

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
class MainViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
    private val getCalculateExpensesUseCase: CalculateExpensesUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = getInitState())
    val uiState = _uiState.asStateFlow()

    private val filterCategory = MutableStateFlow<ExpenseCategory?>(value = null)

    init {
        subscribeOnTransactionsUpdate()
    }

    private fun getInitState(): MainScreenState {
        return MainScreenState(
            transactions = persistentListOf(),
            availableCategories = persistentListOf(
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
            ),
            summaryState = MainScreenState.SummaryState(
                total = "0",
                totalCategories = persistentMapOf()
            ),
            onAddTransactionClick = ::addTransaction,
            onFilterByCategoryClick = ::filterByCategory
        )
    }

    private fun subscribeOnTransactionsUpdate() {
        combine(
            flow = getAllTransactionsUseCase(),
            flow2 = filterCategory
        ) { transactions, filterCategory ->
            if (filterCategory == null) {
                transactions
            } else {
                transactions.filter { it.expenseCategory == filterCategory }
            } to filterCategory
        }
            .onEach {
                val (transactions, filterCategory) = it

                _uiState.value = _uiState.value.copy(
                    transactions = transactions.reversed().toImmutableList(),
                    summaryState = MainScreenState.SummaryState(
                        total = getCalculateExpensesUseCase(
                            startTimestamp = Clock.System.now()
                                .toEpochMilliseconds() - 24 * 60 * 60 * 1000,
                            endTimestamp = Clock.System.now().toEpochMilliseconds(),
                            category = filterCategory,
                        ).toString(),
                        totalCategories = if (filterCategory == null) {
                            transactions.map(Transaction::expenseCategory)
                                .associateWith {
                                    getCalculateExpensesUseCase(
                                        startTimestamp = Clock.System.now()
                                            .toEpochMilliseconds() - 24 * 60 * 60 * 1000,
                                        endTimestamp = Clock.System.now().toEpochMilliseconds(),
                                        category = it
                                    ).toString()
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

    private fun addTransaction(name: String, expenseCategory: ExpenseCategory, amount: String) {
        viewModelScope.launch {
            addTransactionUseCase(name, expenseCategory, amount.toDouble())
        }
    }

    private fun filterByCategory(category: ExpenseCategory?) {
        filterCategory.value = category
    }
}