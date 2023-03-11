package by.lebedev.exchanger.ui.converter

import android.content.Context
import by.lebedev.exchanger.*
import by.lebedev.exchanger.ui.utils.roundBigDecimalString
import by.lebedev.exchanger.ui.utils.roundToTwoDigits
import com.airbnb.epoxy.TypedEpoxyController
import com.airbnb.epoxy.carousel
import javax.inject.Inject

class ConverterController @Inject constructor(
    private val ctx: Context,
) : TypedEpoxyController<ConverterViewState>() {

    var callbacks: Callbacks? = null

    interface Callbacks {
        fun onSubmitClick()
        fun onCurrencyForSellChanged(currency: String)
        fun onAmountForSellChanged(amount: String)
        fun onCurrencyForReceiveChanged(currency: String)
    }

    override fun buildModels(data: ConverterViewState) {

        buildMyBalanceTitle()

        if (data.customerBalances.isNotEmpty()) {
            buildBalanceCarousel(data)
        }

        buildCurrencyExchangeTitle()
        buildSellCurrencies(data)
        buildReceiveCurrencies(data)
        buildSubmitButton(data)

    }

    private fun buildReceiveCurrencies(data: ConverterViewState) {
        receiveCurrency {
            id("receive")
            items(data.currenciesForReceive)
            selectedCurrency(data.selectedCurrencyForReceive)
            onCurrencyChanged {
                this@ConverterController.callbacks?.onCurrencyForReceiveChanged(it)
            }
            receiveAmount(data.amountToReceive.roundBigDecimalString())
        }
    }

    private fun buildSellCurrencies(data: ConverterViewState) {
        sellCurrency {
            id("sell")
            items(data.currenciesForSell)
            selectedCurrency(data.selectedCurrencyForSell)
            amount(data.selectedAmountForSell)
            onCurrencyChanged {
                this@ConverterController.callbacks?.onCurrencyForSellChanged(it)
            }
            onAmountChanged {
                this@ConverterController.callbacks?.onAmountForSellChanged(it)
            }
        }
    }

    private fun buildSubmitButton(data: ConverterViewState) {
        button {
            id("submit_btn")
            text(this@ConverterController.ctx.getString(R.string.submit))
            isEnabled(data.isSubmitEnabled)
            onClick { _ ->
                this@ConverterController.callbacks?.onSubmitClick()
            }
        }
    }

    private fun buildCurrencyExchangeTitle() {
        title {
            id("curr_exchange")
            text(this@ConverterController.ctx.getString(R.string.currency_exchange))
            setAllCaps(true)
        }
    }

    private fun buildMyBalanceTitle() {
        title {
            id("my_balances")
            text(this@ConverterController.ctx.getString(R.string.my_balances))
            setAllCaps(true)
        }
    }

    private fun buildBalanceCarousel(data: ConverterViewState) {
        val models = mutableListOf<BalanceCurrencyBindingModel_>().apply {
            data.customerBalances.forEach { (currency, balance) ->
                add(
                    BalanceCurrencyBindingModel_()
                        .id("currency_$currency")
                        .text("${balance.roundToTwoDigits()} $currency")
                        .balance(balance)
                )
            }
            sortByDescending { it.balance() > 0.0 }
        }

        carousel {
            id("balance_carousel")
            models(models)
            numViewsToShowOnScreen(2f)
        }
    }
}