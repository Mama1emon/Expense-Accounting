package domain

import domain.appcurrency.AppCurrency
import domain.rate.RateRepository

/**
 * @author Andrew Khokhlov on 10/07/2024
 */
class CalculateExpensesUseCase(
    private val transactionsRepository: TransactionsRepository,
    private val rateRepository: RateRepository,
) {

    suspend operator fun invoke(
        category: ExpenseCategory?,
        startTimestamp: Long,
        endTimestamp: Long,
        appCurrency: AppCurrency,
    ): Double {
        val transactions = transactionsRepository.getCategoryTransactions(
            category = category,
            startTimestamp = startTimestamp,
            endTimestamp = endTimestamp
        )

        val rates = transactions
            .map { it.amount.currency }
            .associateWith {
                if (it == appCurrency) {
                    1.0f
                } else {
                    rateRepository.getRate(from = it, to = appCurrency)
                }
            }

        return transactions
            .map(Transaction::amount)
            .sumOf {
                it.value * rates[it.currency]!!
            }
    }
}