package by.lebedev.exchanger.domain.convertion


import by.lebedev.exchanger.data.network.utils.AppCoroutineDispatchers
import by.lebedev.exchanger.data.network.utils.UseCase
import javax.inject.Inject

class ConvertCurrenciesUseCase @Inject constructor(
    dispatchers: AppCoroutineDispatchers
) : UseCase<ConvertCurrenciesUseCase.Params, Map<String, Double>>() {
    override val dispatcher = dispatchers.io

    data class Params(
        val balances: Map<String, Double>,
        val sellAmount: Double,
        val receiveAmount: Double,
        val sellCurrency: String,
        val receiveCurrency: String,
        val feeAmount: Double = 0.0
    )

    override suspend fun doWork(params: Params): Map<String, Double> {
        return convert(params)
    }
    fun convert(params: Params): Map<String, Double> {
        return params.balances.toMutableMap().apply {

            val receiveAmountAfterFee =
                params.receiveAmount.minus(params.feeAmount)

            put(
                params.sellCurrency,
                getOrDefault(params.sellCurrency, 0.0).minus(params.sellAmount)
            )
            put(
                params.receiveCurrency,
                getOrDefault(params.receiveCurrency, 0.0).plus(receiveAmountAfterFee)
            )
        }
    }
}