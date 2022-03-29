package dev.atick.core.utils.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

fun <T> Flow<T>.stateInDelayed(
    initialValue: T,
    scope: CoroutineScope
): StateFlow<T> {
    return this.stateIn(
        scope = scope,
        initialValue = initialValue,
        started = SharingStarted.WhileSubscribed(5000L)
    )
}

fun <T> Flow<T>.shareInDelayed(scope: CoroutineScope): SharedFlow<T> {
    return this.shareIn(
        scope = scope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )
}