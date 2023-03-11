package by.lebedev.exchanger.data.network.utils

import java.util.concurrent.TimeUnit

sealed class Timeouts {
    abstract val connect: Long
    abstract val read: Long
    abstract val write: Long
    abstract val timeUnit: TimeUnit

    object Default : Timeouts() {
        override val connect = 30L
        override val read = 60L // 1 min
        override val write = 60L
        override val timeUnit = TimeUnit.SECONDS
    }
}
