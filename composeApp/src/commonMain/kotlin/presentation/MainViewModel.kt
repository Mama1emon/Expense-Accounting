package presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import domain.AddTransactionUseCase
import domain.CalculateExpensesByCategoryUseCase
import domain.ExpenseCategory
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import presentation.state.MainScreenState

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
class MainViewModel(
    private val addTransactionUseCase: AddTransactionUseCase,
    private val calculateExpensesByCategoryUseCase: CalculateExpensesByCategoryUseCase,
) : ViewModel() {

    private val uiState = MutableStateFlow(value = getInitState())

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

    private fun addTransaction(name: String, expenseCategory: ExpenseCategory, amount: Double) {
        viewModelScope.launch {
            addTransactionUseCase(name, expenseCategory, amount)
        }
    }
}