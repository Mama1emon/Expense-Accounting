package domain

interface TransactionRepository {

    suspend fun saveTransaction(transaction: Transaction)

    suspend fun getCategoryTransactions(
        category: ExpenseCategory,
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Transaction>
}
