package io.github.briangits.events.integration.broker.kafka.consumer.relay

import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.consumer.Handler

internal interface Consumer {
    suspend fun start()
    suspend fun close()

    suspend fun consume(route: Route, handler: Handler)
}

internal expect fun Consumer(options: ConsumerOptions): Consumer
