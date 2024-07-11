package data.converters

import domain.ExpenseCategory
import com.mama1emon.exac.ExpenseCategory as ProtoExpenseCategory

/**
 * @author Andrew Khokhlov on 11/07/2024
 */
object ExpenseCategoryConverter {

    fun convert(expenseCategory: ExpenseCategory): ProtoExpenseCategory {
        return when (expenseCategory) {
            ExpenseCategory.Cellular -> ProtoExpenseCategory.CELLULAR
            ExpenseCategory.Clothing -> ProtoExpenseCategory.CLOTHING
            ExpenseCategory.Documents -> ProtoExpenseCategory.DOCUMENTS
            ExpenseCategory.Entertainment -> ProtoExpenseCategory.ENTERTAINMENT
            ExpenseCategory.Food -> ProtoExpenseCategory.FOOD
            ExpenseCategory.FoodDelivery -> ProtoExpenseCategory.FOOD_DELIVERY
            ExpenseCategory.Gifts -> ProtoExpenseCategory.GIFTS
            ExpenseCategory.HealthAndCare -> ProtoExpenseCategory.HEALTH_AND_CARE
            ExpenseCategory.Household -> ProtoExpenseCategory.HOUSEHOLD
            ExpenseCategory.Housing -> ProtoExpenseCategory.HOUSING
            ExpenseCategory.Sport -> ProtoExpenseCategory.SPORT
            ExpenseCategory.Travel -> ProtoExpenseCategory.TRAVEL
            ExpenseCategory.Transport -> ProtoExpenseCategory.TRANSPORT
            ExpenseCategory.Other -> ProtoExpenseCategory.OTHER
            ExpenseCategory.Unspecified -> ProtoExpenseCategory.EXPENSE_CATEGORY_UNSPECIFIED
        }
    }

    fun convert(protoExpenseCategory: ProtoExpenseCategory): ExpenseCategory {
        return when (protoExpenseCategory) {
            ProtoExpenseCategory.CELLULAR -> ExpenseCategory.Cellular
            ProtoExpenseCategory.CLOTHING -> ExpenseCategory.Clothing
            ProtoExpenseCategory.DOCUMENTS -> ExpenseCategory.Documents
            ProtoExpenseCategory.ENTERTAINMENT -> ExpenseCategory.Entertainment
            ProtoExpenseCategory.FOOD -> ExpenseCategory.Food
            ProtoExpenseCategory.FOOD_DELIVERY -> ExpenseCategory.FoodDelivery
            ProtoExpenseCategory.GIFTS -> ExpenseCategory.Gifts
            ProtoExpenseCategory.HEALTH_AND_CARE -> ExpenseCategory.HealthAndCare
            ProtoExpenseCategory.HOUSEHOLD -> ExpenseCategory.Household
            ProtoExpenseCategory.HOUSING -> ExpenseCategory.Housing
            ProtoExpenseCategory.SPORT -> ExpenseCategory.Sport
            ProtoExpenseCategory.TRAVEL -> ExpenseCategory.Travel
            ProtoExpenseCategory.TRANSPORT -> ExpenseCategory.Transport
            ProtoExpenseCategory.OTHER -> ExpenseCategory.Other
            ProtoExpenseCategory.EXPENSE_CATEGORY_UNSPECIFIED -> ExpenseCategory.Unspecified
        }
    }

}