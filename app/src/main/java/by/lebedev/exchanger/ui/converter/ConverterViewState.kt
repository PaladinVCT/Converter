package by.lebedev.exchanger.ui.converter


import by.lebedev.exchanger.BuildConfig
import by.lebedev.exchanger.ui.utils.toSafeDouble
import com.airbnb.mvrx.MavericksState
import kotlinx.coroutines.Job

data class ConverterViewState(
    val isLoading: Boolean = false,
    val customerBalances: Map<String, Double> = emptyMap(),
    val exchangeRates: Map<String, Double> = emptyMap(),
    val feePercent: Double? = null,
    val feeFrequency: Int? = null,
    val selectedAmountForSell: String? = null,
    val selectedCurrencyForSell: String? = null,
    val selectedCurrencyForReceive: String? = null,
    val autoUpdateJob: Job? = null
) : MavericksState {

    val currenciesForSell: List<String> = run {
        customerBalances.filter {
            it.value > 0
        }.keys.toList()
    }

    val currenciesForReceive: List<String> = run {
        customerBalances.filter {
            it.key != selectedCurrencyForSell
        }.keys.toList()
    }

    val amountToReceive = run {
        if (selectedCurrencyForReceive.isNullOrEmpty()
            || selectedCurrencyForSell.isNullOrEmpty()
        ) return@run 0.0
        if (selectedCurrencyForSell == BuildConfig.INIT_CURRENCY) {
            exchangeRates.filter { it.key == selectedCurrencyForReceive }.values.toList()
                .getOrNull(0)?.times(selectedAmountForSell.toSafeDouble()) ?: 0.0
        } else
            selectedAmountForSell.toSafeDouble().div(
                exchangeRates.filter { it.key == selectedCurrencyForSell }.values.toList()
                    .getOrNull(0) ?: 0.0
            ).times(
                exchangeRates.filter { it.key == selectedCurrencyForReceive }.values.toList()
                    .getOrNull(0) ?: 0.0
            )
    }

    val isSubmitEnabled = run {
        selectedAmountForSell != null
                && selectedCurrencyForReceive != null
                && (customerBalances.filter { it.key == selectedCurrencyForSell }.values.toList()
            .getOrNull(0) ?: 0.0) >= selectedAmountForSell.toSafeDouble()
                && selectedAmountForSell.toSafeDouble() != 0.0
    }
}