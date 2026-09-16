package io.github.briangits.events.integration.broker.kafka.consumer

import io.github.briangits.events.integration.broker.Route
import io.github.briangits.events.integration.broker.consumer.Handler
import io.github.briangits.events.integration.broker.consumer.MessageConsumer
import io.github.briangits.events.integration.broker.kafka.consumer.config.KafkaConsumerConfig
import io.github.briangits.events.integration.broker.kafka.consumer.relay.Consumer
import io.github.briangits.events.integration.broker.kafka.consumer.relay.ConsumerOptions
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.withContext

class KafkaConsumer(
    val brokers: List<String>,
    val groupId: String,
    configBlock: KafkaConsumerConfig.() -> Unit = {}
) : MessageConsumer {
    private val config = KafkaConsumerConfig(brokers, groupId) { configBlock() }
    private val consumer: Consumer by lazy {
        val config = ConsumerOptions(
            brokers = this.config.brokers,
            groupId = this.config.groupId,
            consumerId = this.config.consumerId,
            pollTimeout = this.config.pollTimeout,
            dispatcher = this.config.dispatcher
        )

        Consumer(config)
    }

    override suspend fun start() = consumer.start()

    override suspend fun close() = consumer.close()

    override suspend fun subscribe(
        route: Route,
        handler: Handler
    ) {
        consumer.consume(route) { message ->
            var attempts = 0
            val maxAttempts = config.retries.attempts

            do {
                try {
                    handler(message)
                    break
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Throwable) {
                    attempts++

                    if (attempts >= maxAttempts) {
                        withContext(config.dispatcher) {
                            config.retries.dlq(e, message)
                        }
                    }
                }
            } while (attempts < maxAttempts)
        }
    }
}
