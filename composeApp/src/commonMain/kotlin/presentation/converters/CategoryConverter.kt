package presentation.converters

import domain.ExpenseCategory

/**
 * @author Andrew Khokhlov on 16/07/2024
 */
object CategoryConverter {

    fun convert(category: ExpenseCategory): String {
        return when (category) {
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
    }
}