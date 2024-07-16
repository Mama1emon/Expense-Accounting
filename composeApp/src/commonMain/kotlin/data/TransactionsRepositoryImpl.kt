package data

import androidx.datastore.core.DataStore
import com.mama1emon.exac.Transactions
import data.converters.ExpenseCategoryConverter
import data.converters.TransactionConverter
import domain.ExpenseCategory
import domain.Transaction
import domain.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

class TransactionsRepositoryImpl(
    private val transactionsStore: DataStore<Transactions>,
) : TransactionsRepository {

    override suspend fun saveTransaction(transaction: Transaction) {
        val protoTransaction = TransactionConverter.convert(transaction)

        transactionsStore.updateData { it.copy(transactions = it.transactions + protoTransaction) }
    }

    override suspend fun getCategoryTransactions(
        category: ExpenseCategory?,
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Transaction> {
        val protoCategory = category?.let { ExpenseCategoryConverter.convert(it) }
        return transactionsStore.data.firstOrNull()
            ?.transactions.orEmpty()
            .filter {
                val isSelectedCategory = if (protoCategory == null) {
                    true
                } else {
                    it.expense_category == protoCategory
                }

                isSelectedCategory && it.timestamp in startTimestamp..endTimestamp
            }
            .map(TransactionConverter::convert)
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionsStore.data.map {
            it.transactions.map(TransactionConverter::convert)
        }
    }

    override suspend fun changeTransaction(transaction: Transaction) {
        val protoTransaction = TransactionConverter.convert(transaction)

        transactionsStore.updateData { data ->
            val index = data.transactions.indexOfFirst { it.id == protoTransaction.id }

            if (index != -1) {
                val newTransactions = data.transactions.toMutableList().apply {
                    set(index, protoTransaction)
                }

                data.copy(transactions = newTransactions)
            } else {
                data
            }
        }
    }

    override suspend fun deleteTransaction(id: String) {
        transactionsStore.updateData { data ->
            val index = data.transactions.indexOfFirst { it.id == id }

            if (index != -1) {
                val newTransactions = data.transactions.toMutableList().apply {
                    removeAt(index)
                }

                data.copy(transactions = newTransactions)
            } else {
                data
            }
        }
    }
}