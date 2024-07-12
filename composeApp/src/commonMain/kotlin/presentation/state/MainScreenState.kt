package presentation.state

import domain.ExpenseCategory
import domain.Transaction
import kotlinx.collections.immutable.ImmutableList

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
data class MainScreenState(
    val transactions: ImmutableList<Transaction>,
    val availableCategories: ImmutableList<ExpenseCategory>,
    val onAddTransactionClick: (String, ExpenseCategory, String) -> Unit,
)