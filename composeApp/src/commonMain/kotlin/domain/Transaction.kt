package domain

/**
 * @author Andrew Khokhlov on 10/07/2024
 */
data class Transaction(
    val id: String,
    val name: String,
    val expenseCategory: ExpenseCategory,
    val amount: Double,
    val timestamp: Long,
)