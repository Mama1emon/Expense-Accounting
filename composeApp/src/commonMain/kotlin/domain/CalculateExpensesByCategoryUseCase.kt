package domain

/**
 * @author Andrew Khokhlov on 10/07/2024
 */
class CalculateExpensesByCategoryUseCase(
    private val transactionsRepository: TransactionsRepository,
) {

    suspend operator fun invoke(
        category: ExpenseCategory,
        startTimestamp: Long,
        endTimestamp: Long,
    ): Double {
        return transactionsRepository
            .getCategoryTransactions(category, startTimestamp, endTimestamp)
            .map(Transaction::amount)
            .sum()
    }
}