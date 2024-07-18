package domain

import kotlinx.coroutines.flow.Flow

interface TransactionsRepository {

    suspend fun saveTransaction(transaction: Transaction)

    suspend fun getCategoryTransactions(
        category: ExpenseCategory?,
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Transaction>

    fun getAllTransactions(): Flow<List<Transaction>>

    suspend fun changeTransaction(
        id: String,
        name: String,
        expenseCategory: ExpenseCategory,
        amount: Amount,
        timestamp: Long
    )

    suspend fun deleteTransaction(id: String)
}
