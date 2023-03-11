package by.lebedev.exchanger.ui.converter


import android.content.Context
import android.content.SharedPreferences
import by.lebedev.exchanger.BuildConfig
import by.lebedev.exchanger.R
import by.lebedev.exchanger.data.network.utils.Event
import by.lebedev.exchanger.data.network.utils.trackError
import by.lebedev.exchanger.data.network.utils.trackLoading
import by.lebedev.exchanger.data.network.utils.trackSuccess
import by.lebedev.exchanger.di.mvrx.AssistedViewModelFactory
import by.lebedev.exchanger.di.mvrx.hiltMavericksViewModelFactory
import by.lebedev.exchanger.domain.convertion.ConvertCurrenciesUseCase
import by.lebedev.exchanger.domain.exchangerates.LoadExchangeRatesUseCase
import by.lebedev.exchanger.ui.BaseViewModel
import by.lebedev.exchanger.ui.utils.*
import com.airbnb.mvrx.MavericksViewModelFactory
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.util.*
import kotlin.time.Duration.Companion.seconds


class ConverterViewModel @AssistedInject constructor(
    @Assisted initialState: ConverterViewState,
    private val loadExchangeRatesUseCase: LoadExchangeRatesUseCase,
    private val convertCurrenciesUseCase: ConvertCurrenciesUseCase,
    private val securedPrefs: SharedPreferences,
    private val ctx: Context
) : BaseViewModel<ConverterViewState>(initialState) {

    init {
        loadExchangeRates(true)
        fetchRemoteConfig()
    }

    private fun fetchRemoteConfig() {
        setState {
            copy(
                feePercent = BuildConfig.FEE_PERCENT,
                feeFrequency = BuildConfig.FEE_FREQUENCY
            )
        }
    }

    private fun loadExchangeRates(initBalance: Boolean = false) {
        loadExchangeRatesUseCase.invoke(Unit)
            .trackLoading { setState { copy(isLoading = it) } }
            .trackError(errorEventBus)
            .trackSuccess {
                setState {
                    copy(
                        exchangeRates = it.rates
                    )
                }
                if (initBalance) {
                    setupStartingBalance(
                        rates = it.rates,
                        initCurrency = BuildConfig.INIT_CURRENCY,
                        initCurrencyBalance = BuildConfig.INIT_CURRENCY_BALANCE
                    )
                }
            }
            .launch()
    }

    private fun makeConversion() = withState { state ->
        val params = assembleConversionParams(state)
        convertCurrenciesUseCase.invoke(params)
            .trackLoading { setState { copy(isLoading = it) } }
            .trackError(errorEventBus)
            .trackSuccess {
                securedPrefs.incrementConversionCount()
                setState {
                    copy(
                        customerBalances = it,
                        selectedAmountForSell = null,
                        selectedCurrencyForSell = null,
                        selectedCurrencyForReceive = null,
                    )
                }
                messageEvent.postValue(Event(assembleMessageCluster(params)))
            }
            .launch()
    }

    private fun setupStartingBalance(
        rates: Map<String, Double>,
        initCurrency: String,
        initCurrencyBalance: Double
    ) {
        val customerBalances = mutableMapOf<String, Double>().apply {
            rates.forEach { (currency, _) ->
                put(
                    currency,
                    if (initCurrency == currency || currency == "USD") initCurrencyBalance else 0.0
                )
            }
        }
        setState { copy(customerBalances = customerBalances) }
    }

    fun onCurrencyForSellChanged(currency: String) = withState { state ->
        if (state.selectedCurrencyForReceive == currency)
            setState { copy(selectedCurrencyForReceive = null) }
        setState { copy(selectedCurrencyForSell = currency) }
    }

    fun onCurrencyForReceiveChanged(currency: String) {
        setState { copy(selectedCurrencyForReceive = currency) }
    }

    fun onAmountForSellChanged(amount: String) {
        setState { copy(selectedAmountForSell = amount) }
    }

    fun onSubmitClick() {
        makeConversion()
    }

    private fun assembleConversionParams(state: ConverterViewState): ConvertCurrenciesUseCase.Params {
        return ConvertCurrenciesUseCase.Params(
            balances = state.customerBalances,
            sellAmount = state.selectedAmountForSell.toSafeDouble(),
            receiveAmount = state.amountToReceive.roundToTwoDigits(),
            sellCurrency = state.selectedCurrencyForSell.orEmpty(),
            receiveCurrency = state.selectedCurrencyForReceive.orEmpty(),
            feeAmount = if (resolveHasFee(securedPrefs.getConversionCount())) (state.amountToReceive.roundToTwoDigits()
                .times((state.feePercent ?: 0.0).div(100))).roundToTwoDigits() else 0.0
        )
    }

    private fun assembleMessageCluster(params: ConvertCurrenciesUseCase.Params): MessageCluster {

        val message = if (params.feeAmount == 0.0) ctx.getString(
            R.string.conversion_message,
            "${params.sellAmount} ${params.sellCurrency}",
            "${params.receiveAmount} ${params.receiveCurrency}"
        )
        else ctx.getString(
            R.string.conversion_message_with_fee,
            "${params.sellAmount} ${params.sellCurrency}",
            "${params.receiveAmount} ${params.receiveCurrency}",
            "${params.feeAmount} ${params.receiveCurrency}"
        )
        return MessageCluster(
            titleRes = R.string.currency_converted,
            message = message,
            buttonRes = R.string.done
        )
    }

    private fun resolveHasFee(conversionsCount: Int): Boolean {
        return (conversionsCount > 5 && conversionsCount != 0)
    }

    fun startAutoUpdateJob() = withState { state ->
        if (state.autoUpdateJob == null) {
            val autoUpdateJob = Ticker().tickerFlow(5.seconds)
                .map { Calendar.getInstance().timeInMillis }
                .distinctUntilChanged { old, new ->
                    old == new
                }
                .onEach { loadExchangeRates() }
                .launchIn(viewModelScope)
            setState { copy(autoUpdateJob = autoUpdateJob) }
        }
    }

    fun cancelAutoUpdateJob() = withState { state ->
        state.autoUpdateJob?.cancel()
        setState { copy(autoUpdateJob = null) }
    }


    @AssistedFactory
    interface Factory : AssistedViewModelFactory<ConverterViewModel, ConverterViewState> {
        override fun create(state: ConverterViewState): ConverterViewModel
    }

    companion object :
        MavericksViewModelFactory<ConverterViewModel, ConverterViewState> by hiltMavericksViewModelFactory()
}