package domain.selectedmonth

import kotlinx.datetime.format.MonthNames

/**
 * @author Andrew Khokhlov on 20/07/2024
 */
data class SelectedMonth(val number: Int) {

    val fullName: String by lazy {
        MonthNames.ENGLISH_FULL.names[number - 1]
    }

    init {
        require(number in 1..12)
    }

    constructor(fullName: String) : this(
        number = MonthNames.ENGLISH_FULL.names.indexOf(fullName).let {
            require(it != -1)

            it + 1
        }
    )
}
