package by.lebedev.exchanger.data.repository.user

import by.lebedev.exchanger.data.entities.exchangerates.ExchangeRates
import by.lebedev.exchanger.data.mappers.exchangerates.ExchangeRatesResponseMapper
import by.lebedev.exchanger.data.network.ExchangeRatesService
import by.lebedev.exchanger.data.network.utils.Result
import by.lebedev.exchanger.data.network.utils.RetrofitRunner

import javax.inject.Inject
import javax.inject.Provider


class ExchangeRatesDataSource @Inject constructor(
    private val service: Provider<ExchangeRatesService>,
    private val retrofitRunner: RetrofitRunner,
    private val exchangeRatesResponseMapper: ExchangeRatesResponseMapper

) {

    suspend fun loadExchangeRates(): Result<ExchangeRates> {
        return retrofitRunner.invoke(exchangeRatesResponseMapper) {
            service.get().loadExchangeRates()
        }
    }

}