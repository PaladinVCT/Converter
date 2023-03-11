package by.lebedev.exchanger.domain.exchangerates


import by.lebedev.exchanger.data.entities.exchangerates.ExchangeRates
import by.lebedev.exchanger.data.network.utils.AppCoroutineDispatchers
import by.lebedev.exchanger.data.network.utils.UseCase
import by.lebedev.exchanger.data.repository.user.ExchangeRatesRepository
import javax.inject.Inject

class LoadExchangeRatesUseCase @Inject constructor(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    dispatchers: AppCoroutineDispatchers
) : UseCase<Unit, ExchangeRates>() {
    override val dispatcher = dispatchers.io

    override suspend fun doWork(params: Unit): ExchangeRates {
        return exchangeRatesRepository.loadExchangeRates()
    }
}