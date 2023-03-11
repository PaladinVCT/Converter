package by.lebedev.exchanger.ui.converter

import by.lebedev.exchanger.data.network.utils.AppCoroutineDispatchers
import by.lebedev.exchanger.domain.convertion.ConvertCurrenciesUseCase
import junit.framework.Assert.assertEquals
import kotlinx.coroutines.Dispatchers
import org.junit.Before
import org.junit.Test


class ConverterUnitTest {

    private lateinit var dispatchers: AppCoroutineDispatchers
    private lateinit var balance: Map<String, Double>
    private lateinit var params: ConvertCurrenciesUseCase.Params
    private val sellCurrency = "EUR"
    private val sellAmount = 200.0
    private val receiveCurrency = "AUD"
    private val receiveAmount = 220.9

    @Before
    fun setup_dispatchers() {
        dispatchers = AppCoroutineDispatchers(Dispatchers.IO, Dispatchers.Default, Dispatchers.Main)
    }

    @Before
    fun setup_balances() {
        balance = mapOf("EUR" to 1000.0, "USD" to 500.0, "JPY" to 3500.50, "AUD" to 0.0)
    }

    @Before
    fun setup_params() {
        params = ConvertCurrenciesUseCase.Params(
            balance,
            sellAmount,
            receiveAmount,
            sellCurrency,
            receiveCurrency
        )
    }

    @Test
    fun convert_isCorrect() {
        assertEquals(
            mapOf("EUR" to 800.0, "USD" to 500.0, "JPY" to 3500.50, "AUD" to 220.9),
            ConvertCurrenciesUseCase(dispatchers).convert(params)
        )
    }
}