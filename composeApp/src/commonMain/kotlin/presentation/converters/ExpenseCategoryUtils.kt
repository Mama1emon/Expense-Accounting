package presentation.converters

import domain.ExpenseCategory

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
object ExpenseCategoryUtils {

    fun valueOf(value: String): ExpenseCategory {
        return when (value) {
            "Housing" -> ExpenseCategory.Housing
            "Transport" -> ExpenseCategory.Transport
            "Cellular" -> ExpenseCategory.Cellular
            "Entertainment" -> ExpenseCategory.Entertainment
            "Food" -> ExpenseCategory.Food
            "Food delivery" -> ExpenseCategory.FoodDelivery
            "Gifts" -> ExpenseCategory.Gifts
            "Health and care" -> ExpenseCategory.HealthAndCare
            "Household" -> ExpenseCategory.Household
            "Sport" -> ExpenseCategory.Sport
            "Travel" -> ExpenseCategory.Travel
            "Clothing" -> ExpenseCategory.Clothing
            "Documents" -> ExpenseCategory.Documents
            "Other" -> ExpenseCategory.Other
            "Unspecified" -> ExpenseCategory.Unspecified
            else -> error("Unknown ExpenseCategory: $value")
        }
    }
}

val ExpenseCategory.name: String
    get() = when (this) {
        ExpenseCategory.Housing -> "Housing"
        ExpenseCategory.Transport -> "Transport"
        ExpenseCategory.Cellular -> "Cellular"
        ExpenseCategory.Entertainment -> "Entertainment"
        ExpenseCategory.Food -> "Food"
        ExpenseCategory.FoodDelivery -> "Food delivery"
        ExpenseCategory.Gifts -> "Gifts"
        ExpenseCategory.HealthAndCare -> "Health and care"
        ExpenseCategory.Household -> "Household"
        ExpenseCategory.Sport -> "Sport"
        ExpenseCategory.Travel -> "Travel"
        ExpenseCategory.Clothing -> "Clothing"
        ExpenseCategory.Documents -> "Documents"
        ExpenseCategory.Other -> "Other"
        ExpenseCategory.Unspecified -> "Unspecified"
    }