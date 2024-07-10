package domain

sealed interface ExpenseCategory {

    data object Housing : ExpenseCategory

    data object Transport : ExpenseCategory

    data object Cellular : ExpenseCategory

    data object Sport : ExpenseCategory

    data object HealthAndCare : ExpenseCategory

    data object Household : ExpenseCategory

    data object Clothing : ExpenseCategory

    data object Entertainment : ExpenseCategory

    data object Gifts : ExpenseCategory

    data object Documents : ExpenseCategory

    data object Travel : ExpenseCategory

    data object FoodDelivery : ExpenseCategory

    data object Food : ExpenseCategory

    data object Other : ExpenseCategory
}
