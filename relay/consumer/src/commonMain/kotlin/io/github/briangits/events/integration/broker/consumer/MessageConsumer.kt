package io.github.briangits.events.integration.broker.consumer

import io.github.briangits.events.integration.broker.Route

interface MessageConsumer {
    suspend fun start()
    suspend fun close()

    suspend fun subscribe(route: Route, handler: Handler)
}
