package by.lebedev.exchanger.data.repository.user



import by.lebedev.exchanger.data.entities.exchangerates.ExchangeRates
import by.lebedev.exchanger.data.network.utils.getOrThrow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesRepository @Inject constructor(
    private val exchangeRatesDataSource: ExchangeRatesDataSource,
) {

    suspend fun loadExchangeRates(): ExchangeRates {
        return exchangeRatesDataSource.loadExchangeRates().getOrThrow()
    }

}