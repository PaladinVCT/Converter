package by.lebedev.exchanger.data.network


import by.lebedev.exchanger.data.dto.exchangerates.ExchangeRatesResponse
import retrofit2.http.GET

interface ExchangeRatesService {

    @GET("currency-exchange-rates")
    suspend fun loadExchangeRates(
    ): ExchangeRatesResponse
}