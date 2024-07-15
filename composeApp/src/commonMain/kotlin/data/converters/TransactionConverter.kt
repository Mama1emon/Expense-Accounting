package data.converters

import domain.Amount
import domain.Transaction
import com.mama1emon.exac.Transaction as ProtoTransaction

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
object TransactionConverter {

    fun convert(protoTransaction: ProtoTransaction): Transaction {
        val appCurrency = CurrencyTypeConverter.convert(protoTransaction.type)

        return Transaction(
            id = protoTransaction.id,
            name = protoTransaction.name,
            expenseCategory = ExpenseCategoryConverter.convert(protoTransaction.expense_category),
            amount = if (appCurrency == null) {
                Amount(value = protoTransaction.amount)
            } else {
                Amount(value = protoTransaction.amount, currency = appCurrency)
            },
            timestamp = protoTransaction.timestamp,
        )
    }

    fun convert(transaction: Transaction): ProtoTransaction {
        return ProtoTransaction(
            id = transaction.id,
            name = transaction.name,
            expense_category = ExpenseCategoryConverter.convert(transaction.expenseCategory),
            amount = transaction.amount.value,
            timestamp = transaction.timestamp,
            type = CurrencyTypeConverter.convert(transaction.amount.currency),
        )
    }
}