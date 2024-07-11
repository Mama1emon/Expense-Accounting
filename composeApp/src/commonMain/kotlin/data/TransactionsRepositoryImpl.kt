package data

import androidx.datastore.core.DataStore
import com.mama1emon.exac.Transactions
import data.converters.ExpenseCategoryConverter
import domain.ExpenseCategory
import domain.Transaction
import domain.TransactionsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import com.mama1emon.exac.Transaction as ProtoTransaction

class TransactionsRepositoryImpl(
    private val transactionsStore: DataStore<Transactions>,
) : TransactionsRepository {

    override suspend fun saveTransaction(transaction: Transaction) {
        val protoTransaction = ProtoTransaction(
            id = transaction.id,
            name = transaction.name,
            expense_category = ExpenseCategoryConverter.convert(transaction.expenseCategory),
            amount = transaction.amount,
            timestamp = transaction.timestamp,
        )

        transactionsStore.updateData { it.copy(transactions = it.transactions + protoTransaction) }
    }

    override suspend fun getCategoryTransactions(
        category: ExpenseCategory,
        startTimestamp: Long,
        endTimestamp: Long
    ): List<Transaction> {
        val protoCategory = ExpenseCategoryConverter.convert(category)
        return transactionsStore.data.firstOrNull()
            ?.transactions.orEmpty()
            .filter {
                it.expense_category == protoCategory &&
                    it.timestamp in startTimestamp..endTimestamp
            }
            .map { protoTransaction ->
                Transaction(
                    id = protoTransaction.id,
                    name = protoTransaction.name,
                    expenseCategory = ExpenseCategoryConverter.convert(protoTransaction.expense_category),
                    amount = protoTransaction.amount,
                    timestamp = protoTransaction.timestamp,
                )
            }
    }

    override fun getAllTransactions(): Flow<List<Transaction>> {
        return transactionsStore.data.map {
            it.transactions.map { protoTransaction ->
                Transaction(
                    id = protoTransaction.id,
                    name = protoTransaction.name,
                    expenseCategory = ExpenseCategoryConverter.convert(protoTransaction.expense_category),
                    amount = protoTransaction.amount,
                    timestamp = protoTransaction.timestamp,
                )
            }
        }
    }
}