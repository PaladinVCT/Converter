package by.lebedev.exchanger.data.network.utils

enum class ErrorType {
    IO,
    Network,
    NetworkConnection,
    Timeout,
    JsonParsing,
    Unclassified,
    Unauthorized,
}