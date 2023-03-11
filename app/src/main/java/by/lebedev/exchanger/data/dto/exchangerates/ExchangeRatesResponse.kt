package by.lebedev.exchanger.data.dto.exchangerates


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ExchangeRatesResponse(
    @Json(name = "rates")
    val rates: Map<String, Double>? = null
)