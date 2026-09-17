
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertFailsWith

class LifecycleTest {
    @Test
    fun `subscribing after closing the relay`() = runTest {
        val (consumer, relay) = createConsumer()
        relay.close()


        val completion = CompletableDeferred<Unit>()

        val handler = CoroutineExceptionHandler { _, e ->
            completion.completeExceptionally(e)
        }

        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob() + handler)
        val job = scope.launch {
            consumer.subscribe<TestEvent> { }
        }

        job.invokeOnCompletion { e ->
            if (e != null) completion.completeExceptionally(e)
            else completion.complete(Unit)
        }

        assertFailsWith<IllegalStateException> {
            completion.await()
        }
    }
}
