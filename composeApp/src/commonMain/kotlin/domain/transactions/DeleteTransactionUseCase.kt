package domain.transactions

import domain.TransactionsRepository

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
class DeleteTransactionUseCase(
    private val transactionsRepository: TransactionsRepository,
) {

    suspend operator fun invoke(id: String) {
        transactionsRepository.deleteTransaction(id)
    }
}