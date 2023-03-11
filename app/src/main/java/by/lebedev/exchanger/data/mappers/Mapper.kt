

package by.lebedev.exchanger.data.mappers

interface Mapper<F, T> {
    fun map(from: F): T
}

interface IndexedMapper<F, T> {
    fun map(index: Int, from: F): T
}

interface Mapper2Source<F1, F2, T> {
    fun map(from1: F1, from2: F2): T
}

interface Mapper3Source<F1, F2, F3, T> {
    fun map(from1: F1, from2: F2, from3: F3): T
}

interface Mapper4Source<F1, F2, F3, F4, T> {
    fun map(from1: F1, from2: F2, from3: F3, from4: F4): T
}

interface Mapper5Source<F1, F2, F3, F4, F5, T> {
    fun map(from1: F1, from2: F2, from3: F3, from4: F4, from5: F5): T
}


fun <F, T> Mapper<F, T>.mapOrNull(from: F?): T? {
    return from?.let { kotlin.runCatching { map(it) }.getOrNull() }
}

fun <F1, F2, T> Mapper2Source<F1, F2, T>.mapOrNull(from1: F1?, from2: F2): T? {
    return from1?.let { kotlin.runCatching { map(it, from2) }.getOrNull() }
}
