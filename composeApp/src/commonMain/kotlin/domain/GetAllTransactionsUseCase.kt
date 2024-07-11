package domain

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
class GetAllTransactionsUseCase(private val repository: TransactionsRepository) {

    operator fun invoke() = repository.getAllTransactions()
}