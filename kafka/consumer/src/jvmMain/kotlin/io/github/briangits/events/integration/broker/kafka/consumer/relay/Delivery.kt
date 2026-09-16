package io.github.briangits.events.integration.broker.kafka.consumer.relay

import io.github.briangits.events.integration.broker.Message
import kotlinx.coroutines.CompletableDeferred

class Delivery(
    val message: Message,
) {
    private val result = CompletableDeferred<Unit>()

    fun ack()  { result.complete(Unit) }

    fun nack(e: Throwable) {
        result.completeExceptionally(e)
    }

    suspend fun await() = result.await()
}
