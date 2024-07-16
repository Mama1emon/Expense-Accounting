package domain.transactions

import domain.Amount
import domain.ExpenseCategory
import domain.TransactionsRepository

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
class ChangeTransactionUseCase(
    private val transactionsRepository: TransactionsRepository,
) {

    suspend operator fun invoke(
        id: String,
        name: String,
        expenseCategory: ExpenseCategory,
        amount: Amount,
    ) {
        transactionsRepository.changeTransaction(
            id = id,
            name = name,
            expenseCategory = expenseCategory,
            amount = amount
        )
    }
}