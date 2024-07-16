package presentation.state

import domain.ExpenseCategory
import domain.appcurrency.AppCurrency
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.ImmutableSet

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
data class MainScreenState(
    val appCurrency: AppCurrency,
    val topBarState: TopBarState,
    val transactions: ImmutableList<TransactionState>,
    val availableCategories: ImmutableSet<ExpenseCategory>,
    val transactionDetailsState: TransactionDetailsState,
    val summaryState: SummaryState,
) {

    data class TopBarState(
        val availableAppCurrencies: ImmutableSet<AppCurrency>,
        val filterCategories: ImmutableSet<ExpenseCategory>,
        val onChangeAppCurrencyClick: (AppCurrency) -> Unit,
        val onFilterByCategoryClick: (ExpenseCategory?) -> Unit,
    )

    data class TransactionState(
        val id: String,
        val name: String,
        val category: String,
        val amount: String,
        val primaryAmount: String,
    )

    data class TransactionDetailsState(
        val availableCurrencies: ImmutableSet<AppCurrency>,
        val availableCategories: ImmutableSet<ExpenseCategory>,
        val onAddTransactionClick: (String, ExpenseCategory, String, AppCurrency) -> Unit
    )

    data class SummaryState(
        val total: String,
        val totalCategories: ImmutableMap<ExpenseCategory, String>,
    )
}