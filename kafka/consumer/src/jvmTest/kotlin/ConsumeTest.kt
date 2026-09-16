
import io.github.briangits.events.integration.broker.Message
import io.github.briangits.events.integration.broker.Route
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.uuid.Uuid

class ConsumeTest {
    @Test
    fun `consuming messages`() = runTest {
        val topic = Uuid.random().toString()
        val producer = createProducer()

        val consumer = createConsumer()

        val messageKey = Uuid.random().toString()
        val payload = "This is a test message"
        val headers = mapOf("messageType" to "TestMessage")

        producer.send(
            topic = topic,
            key = messageKey,
            payload = payload.encodeToByteArray(),
            headers = headers.mapValues { it.value.encodeToByteArray() }
        )

        val _message = CompletableDeferred<Message>()
        backgroundScope.launch {
            consumer.consume(Route(topic = topic)) {
                _message.complete(it)
            }
        }

        consumer.start()

        val message = _message.await()

        assertEquals(messageKey, message.key)
        assertEquals(payload, message.data.decodeToString())

        headers.forEach { key, value ->
            assertEquals(value, message.metadata[key]?.decodeToString())
        }

        producer.close()
        consumer.close()
    }
}
