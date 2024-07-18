package domain

import utils.randomUUID

/**
 * @author Andrew Khokhlov on 10/07/2024
 */
class AddTransactionUseCase(private val repository: TransactionsRepository) {

    suspend operator fun invoke(
        name: String,
        expenseCategory: ExpenseCategory,
        amount: Amount,
        timestamp: Long,
    ) {
        val transaction = Transaction(
            id = randomUUID(),
            name = name,
            expenseCategory = expenseCategory,
            amount = amount,
            timestamp = timestamp,
        )

        repository.saveTransaction(transaction)
    }
}