package domain

/**
 * @author Andrew Khokhlov on 10/07/2024
 */
class CalculateExpensesByCategoryUseCase(
    private val transactionRepository: TransactionRepository,
) {

    suspend operator fun invoke(
        category: ExpenseCategory,
        startTimestamp: Long,
        endTimestamp: Long,
    ): Double {
        return transactionRepository
            .getCategoryTransactions(category, startTimestamp, endTimestamp)
            .map(Transaction::amount)
            .sum()
    }
}