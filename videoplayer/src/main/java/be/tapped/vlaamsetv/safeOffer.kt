package be.tapped.vlaamsetv

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.SendChannel

internal fun <E> SendChannel<E>.safeOffer(value: E): Boolean = !isClosedForSend && try {
    offer(value)
} catch (e: CancellationException) {
    false
}
