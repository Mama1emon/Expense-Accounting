package domain

import domain.appcurrency.AppCurrency

/**
 * @author Andrew Khokhlov on 15/07/2024
 */
data class Amount(
    val value: Double,
    val currency: AppCurrency,
) {

    constructor(value: Double) : this(value, AppCurrency.Dollar)
}
