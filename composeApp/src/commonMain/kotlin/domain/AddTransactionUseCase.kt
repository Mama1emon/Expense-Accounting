package domain

import kotlinx.datetime.Clock
import utils.randomUUID

/**
 * @author Andrew Khokhlov on 10/07/2024
 */
class AddTransactionUseCase(private val repository: TransactionRepository) {

    suspend operator fun invoke(
        name: String,
        expenseCategory: ExpenseCategory?,
        amount: Double,
    ) {
        val transaction = Transaction(
            id = randomUUID(),
            name = name,
            expenseCategory = expenseCategory,
            amount = amount,
            timestamp = Clock.System.now().toEpochMilliseconds(),
        )

        repository.saveTransaction(transaction)
    }
}