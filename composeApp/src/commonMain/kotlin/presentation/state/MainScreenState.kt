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
    val groupBy: Group,
    val topBarState: TopBarState,
    val transactions: ImmutableList<TransactionState>,
    val availableCategories: ImmutableSet<ExpenseCategory>,
    val transactionDetailsState: TransactionDetailsState,
    val summaryState: SummaryState,
) {

    data class TopBarState(
        val availableGroups: ImmutableSet<Group>,
        val availableAppCurrencies: ImmutableSet<AppCurrency>,
        val filterCategories: ImmutableSet<ExpenseCategory>,
        val onChangeGroupClick: (Group) -> Unit,
        val onChangeAppCurrencyClick: (AppCurrency) -> Unit,
        val onFilterByCategoryClick: (ExpenseCategory?) -> Unit,
    )

    enum class Group { Date, Category, Currency }

    data class TransactionState(
        val id: String,
        val name: String,
        val category: ExpenseCategory,
        val categoryString: String,
        val amount: String,
        val primaryAmount: Double,
        val primaryAmountString: String,
        val primaryCurrency: AppCurrency,
    )

    data class TransactionDetailsState(
        val availableCurrencies: ImmutableSet<AppCurrency>,
        val availableCategories: ImmutableSet<ExpenseCategory>,
        val onAddTransactionClick: (String, ExpenseCategory, String, AppCurrency) -> Unit,
        val onChangeTransactionClick: (TransactionState) -> Unit,
        val onDeleteClick: (String) -> Unit,
    )

    data class SummaryState(
        val total: String,
        val totalCategories: ImmutableMap<ExpenseCategory, String>,
    )
}