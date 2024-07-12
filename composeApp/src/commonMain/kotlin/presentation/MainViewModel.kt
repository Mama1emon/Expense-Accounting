package presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AddTransactionUseCase
import domain.ExpenseCategory
import domain.GetAllTransactionsUseCase
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import presentation.state.MainScreenState

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
class MainViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val getAllTransactionsUseCase: GetAllTransactionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(value = getInitState())
    val uiState = _uiState.asStateFlow()

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
            onAddTransactionClick = ::addTransaction
        )
    }

    private fun subscribeOnTransactionsUpdate() {
        getAllTransactionsUseCase()
            .onEach {
                _uiState.value = _uiState.value.copy(transactions = it.toImmutableList())
            }
            .launchIn(viewModelScope)
    }

    private fun addTransaction(name: String, expenseCategory: ExpenseCategory, amount: String) {
        viewModelScope.launch {
            addTransactionUseCase(name, expenseCategory, amount.toDouble())
        }
    }
}