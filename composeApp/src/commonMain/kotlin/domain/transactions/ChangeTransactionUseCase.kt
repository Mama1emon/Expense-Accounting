package domain.transactions

import domain.Transaction
import domain.TransactionsRepository

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
class ChangeTransactionUseCase(
    private val transactionsRepository: TransactionsRepository,
) {

    suspend operator fun invoke(transaction: Transaction) {
        transactionsRepository.changeTransaction(transaction)
    }
}