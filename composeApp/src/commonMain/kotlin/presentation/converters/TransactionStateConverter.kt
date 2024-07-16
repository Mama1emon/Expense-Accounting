package presentation.converters

import domain.Amount
import domain.Transaction
import presentation.formatters.AmountFormatter
import presentation.state.MainScreenState

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
class TransactionStateConverter(
    private val amount: Amount,
) {

    fun convert(transaction: Transaction): MainScreenState.TransactionState {
        return MainScreenState.TransactionState(
            id = transaction.id,
            name = transaction.name,
            category = CategoryConverter.convert(transaction.expenseCategory),
            amount = AmountFormatter.formatAmount(
                value = amount.value,
                currency = amount.currency,
            ),
            primaryAmount = AmountFormatter.formatAmount(
                value = transaction.amount.value,
                currency = transaction.amount.currency
            )
        )
    }
}