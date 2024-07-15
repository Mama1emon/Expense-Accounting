package presentation.state

import domain.AppCurrency
import domain.ExpenseCategory
import domain.Transaction
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
data class MainScreenState(
    val topBarState: TopBarState,
    val transactions: ImmutableList<Transaction>,
    val availableCategories: ImmutableSet<ExpenseCategory>,
    val summaryState: SummaryState,
    val onAddTransactionClick: (String, ExpenseCategory, String) -> Unit,
) {

    data class TopBarState(
        val selectedAppCurrency: AppCurrency,
        val availableAppCurrencies: ImmutableList<AppCurrency>,
        val filterCategories: ImmutableSet<ExpenseCategory>,
        val onChangeAppCurrencyClick: (AppCurrency) -> Unit,
        val onFilterByCategoryClick: (ExpenseCategory?) -> Unit,
    )

    data class SummaryState(
        val total: String,
        val totalCategories: ImmutableMap<ExpenseCategory, String>,
    )
}