package presentation.state

import domain.ExpenseCategory
import domain.Transaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
data class MainScreenState(
    val transactions: ImmutableList<Transaction>,
    val availableCategories: ImmutableList<ExpenseCategory>,
    val summaryState: SummaryState,
    val onAddTransactionClick: (String, ExpenseCategory, String) -> Unit,
    val onFilterByCategoryClick: (ExpenseCategory?) -> Unit,
) {

    data class SummaryState(
        val total: String,
        val totalCategories: ImmutableMap<ExpenseCategory, String>,
    )
}