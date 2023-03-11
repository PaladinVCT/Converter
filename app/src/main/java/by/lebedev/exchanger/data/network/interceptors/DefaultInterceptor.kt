package by.lebedev.exchanger.data.network.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class DefaultInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val modifiedRequest = request
            .newBuilder()
            .build()
        return chain.proceed(modifiedRequest)
    }
}