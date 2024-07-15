package domain

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
sealed interface AppCurrency {

    data object Dollar : AppCurrency

    data object Euro : AppCurrency

    data object Ruble : AppCurrency

    data object IndonesianRupiah : AppCurrency
}
