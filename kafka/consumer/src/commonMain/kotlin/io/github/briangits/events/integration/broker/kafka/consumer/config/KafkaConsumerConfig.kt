package io.github.briangits.events.integration.broker.kafka.consumer.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class KafkaConsumerConfig(
    val brokers: List<String>,
    val groupId: String,
    var consumerId: String? = null,
    var pollTimeout: Duration = 1.seconds,
    var retries: RetryConfig = RetryConfig(),
    var dispatcher: CoroutineDispatcher = Dispatchers.IO,
    block: KafkaConsumerConfig.() -> Unit = {}
) {
    init { block() }

    fun retries(block: RetryConfig.() -> Unit) {
        retries.block()
    }
}
