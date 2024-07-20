package presentation.state

import kotlinx.collections.immutable.*

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
data class MainScreenState(
    val params: BaseParams,
    val topBarState: TopBarState,
    val transactions: ImmutableList<TransactionItemState>,
    val transactionDetailsState: TransactionDetailsState,
    val summaryState: SummaryState,
) {

    data class BaseParams(
        val appCurrency: String,
        val availableAppCurrencies: ImmutableSet<String>,
        val groupBy: Group,
        val selectedMonth: String,
    )

    data class TopBarState(
        val availableMonths: ImmutableSet<String>,
        val transactionFiltersState: TransactionFiltersState,
        val onChangeMonthClick: (String) -> Unit,
        val onChangeGroupClick: (Group) -> Unit,
        val onChangeAppCurrencyClick: (String) -> Unit,
    ) {
        val availableGroups: ImmutableSet<Group> = Group.entries.toImmutableSet()
    }

    data class TransactionFiltersState(
        val filterCategories: ImmutableSet<String>,
        val filterCurrencies: ImmutableSet<String>,
        val filterStartDate: Long,
        val filterEndDate: Long,
        val onFilterClick: (Filter, String?) -> Unit,
    )

    enum class Group { Date, Category, Currency }

    enum class Filter { Category, Date, Currency }

    sealed interface TransactionItemState {

        data class Title(val value: String) : TransactionItemState

        data class Transaction(
            val id: String,
            val name: String,
            val category: String,
            val amount: String,
            val primaryAmount: Double,
            val primaryAmountString: String,
            val primaryCurrency: String,
            val timestamp: Long,
        ) : TransactionItemState
    }

    data class TransactionDetailsState(
        val availableCategories: ImmutableSet<String>,
        val onAddTransactionClick: (
            name: String,
            category: String,
            amount: String,
            currency: String,
            timestamp: Long,
        ) -> Unit,
        val onChangeTransactionClick: (TransactionItemState.Transaction) -> Unit,
        val onDeleteClick: (String) -> Unit,
        val onChangeMonthClick: (String) -> Unit,
    )

    data class SummaryState(
        val total: String,
        val totalCategories: ImmutableList<TotalCategory>,
    ) {

        data class TotalCategory(val category: String, val total: String)
    }
}