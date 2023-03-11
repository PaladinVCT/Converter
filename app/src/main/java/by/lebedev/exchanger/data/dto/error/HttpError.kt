package by.lebedev.exchanger.data.dto.error

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class HttpError(

    @Json(name = "message")
    val message: String? = null,

    @Json(name = "error")
    val error: String? = null,

    @Json(name = "CODE")
    val code: String? = null,

    @Json(name = "DEV_MESSAGE")
    val devMessage: String? = null,

    @Json(name = "USER_MESSAGE")
    val userMessage: String? = null,

    @Json(name = "CONSTRAINT_VIOLATION_MESSAGE")
    val constraintViolationMessage: Map<String, String?>? = null
) {
    val errorMessage: String =
        userMessage ?: message ?: error ?: devMessage
        ?: constraintViolationMessage?.entries?.firstOrNull()?.toString() ?: "Server error"
}