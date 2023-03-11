package by.lebedev.exchanger.data.mappers.exchangerates


import by.lebedev.exchanger.data.dto.exchangerates.ExchangeRatesResponse
import by.lebedev.exchanger.data.entities.exchangerates.ExchangeRates
import by.lebedev.exchanger.data.mappers.Mapper
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExchangeRatesResponseMapper @Inject constructor() :
    Mapper<ExchangeRatesResponse, ExchangeRates> {
    override fun map(from: ExchangeRatesResponse): ExchangeRates {
        return ExchangeRates(
            rates = from.rates ?: mapOf()
        )
    }
}