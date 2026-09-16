package io.github.briangits.events.integration.broker.kafka.consumer.config

import io.github.briangits.events.integration.broker.Message

typealias DLQHandler = (error: Throwable, message: Message) -> Unit

class RetryConfig(
    var attempts: Int = 3,
    var dlq: DLQHandler = { e, _ -> throw e },
    block: RetryConfig.() -> Unit = {}
) {
    init { block() }

    fun dlq(block: DLQHandler) {
        dlq = block
    }
}