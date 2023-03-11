package by.lebedev.exchanger.di

import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.coroutineScope
import by.lebedev.exchanger.data.network.ExchangeRatesService
import by.lebedev.exchanger.data.network.interceptors.DefaultInterceptor
import by.lebedev.exchanger.data.network.utils.AppCoroutineDispatchers
import by.lebedev.exchanger.data.network.utils.Timeouts
import by.lebedev.exchanger.ui.utils.setupTimeout
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    fun provideLongLifetimeScope(): CoroutineScope {
        return ProcessLifecycleOwner.get().lifecycle.coroutineScope
    }

    @Singleton
    @Provides
    fun provideCoroutineDispatchers() = AppCoroutineDispatchers(
        io = Dispatchers.IO,
        computation = Dispatchers.Default,
        main = Dispatchers.Main
    )

    @Singleton
    @Provides
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Singleton
    @Provides
    fun provideDefaultInterceptor(): DefaultInterceptor {
        return DefaultInterceptor()
    }

    @Singleton
    @Provides
    fun provideMoshiConverter(moshi: Moshi): MoshiConverterFactory {
        return MoshiConverterFactory.create(moshi)
    }

    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder().build()
    }
}

@Module
@InstallIn(SingletonComponent::class)
class ClientModule {

    @Singleton
    @Provides
    fun provideExchangeRatesRetrofit(
        httpClient: OkHttpClient,
        moshiConverter: MoshiConverterFactory
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(by.lebedev.exchanger.BuildConfig.EXCHANGE_RATES_URL)
            .addConverterFactory(moshiConverter)
            .client(httpClient)
            .build()
    }

    @Module
    @InstallIn(SingletonComponent::class)
    class ExchangeRatesServiceModule {
        @Singleton
        @Provides
        fun provideInstaService(
            retrofit: Retrofit
        ): ExchangeRatesService = retrofit.create()
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        defaultInterceptor: DefaultInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {

        return OkHttpClient.Builder()
            .addInterceptor(defaultInterceptor).apply {
                if (by.lebedev.exchanger.BuildConfig.DEBUG)
                    addInterceptor(httpLoggingInterceptor)
            }
            .setupTimeout(Timeouts.Default)
            .build()
    }
}