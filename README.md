<table align="center">
    <tr align="center">
        <td valign="middle">
            <img src="./docs/images/send.png" width="100" alt="events.kt">
        </td>
        <td valign="middle">
            <h1>events.kt</h1>
        </td>
    </tr>
</table>

<p align="center">A broker-agnostic integration event relay for Kotlin.</p>

[![Kotlin](https://img.shields.io/badge/kotlin-2.4.0-blue.svg?logo=kotlin)](http://kotlinlang.org)
[![GitHub License](https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0)

`events.kt` provides a simple, type-safe API for publishing and consuming 
integration events without coupling your application to a specific message broker.

---

## Quick Start

### 1. Define an event

Define an integration event, specifying the topic it's published to and, optionally,
it's name & key property path.

```kotlin
@Serializable
@IntegrationEvent("users", name = "created", key = "user.id")
data class UserCreated(val user: User, createdAt: Instant)
```

### 2. Publish events

Configure a publisher and create an event producer:

```kotlin
val publisher = KafkaPublisher(brokers = listOf("localhost:9092"))
val producer = Producer(publisher) {
    serialization { json() }
}

val user = User(id = 123, name = "Jane Doe")

producer.publish { UserCreated(user, createdAt = Clock.System,now()) }
```

### 2. Consume events

Configure a consumer relay and create an event consumer:

```kotlin
val relay = KafkaConsumer(brokers = listOf("localhost:9092"), groupId = "my-group")
val consumer = Consumer(relay) {
    serialization { json() }
}

consumer.subscribe<UserCreated> { event, metadata ->
    println("User ${event.user.name} created at ${event.createdAt}")
}
```
---

See the [documentation](https://briangits.github.io/events.kt/events.kt/getting-started)
for a complete guide on using the library.

---
## Project Configuration

`events.kt` provides a Gradle plugin that configures code generation for event definitions 
and registration helper functions.

You can also use the library without the Gradle plugin by registering event definitions manually.

See the [manual registration guide](https://briangits.github.io/events.kt/registry/manual-registration) for details.

### Apply the plugin
```kotlin
plugins {
    id("io.github.briangits.events.integration") version "<version>"
}

intagrationEvents {
    packageName = "com.exmaple.events"
    loaderFunctionName = "registerGenratedDefintions"
}
```

Generate the event definitions:

```bash
./gradlew :generateEventDefinitions
```

The generated registration function can then be used by your producers and consumers:

```kotlin
// Producer
val producer = Producer(publisher) { /* Producer config */ }
producer.registerGeneratedDefinitions()

// Consumer
val consumer = Consumer(relay) { /* Consumer config */ }
consumer.registerGeneratedDefinitions()
```

### Creating a Producer
#### 1) Add the dependency to your project

```kotlin
dependencies {
    // Prodcer API
    implementation("io.github.briangits.events:producer:<version>")
    
    // Kafka Publihser
    implementation("io.github.briangits.events:kafka-publisher:<version>")
}
```

#### 2) Create a publisher

A publisher can be shared by one or more producers.

```kotlin
val publisher = KafkaPublisher(brokers = listOf("localhost:9092")) {
    /* Kafka producer configurartin */
}
```

See [creating a publisher](https://briangits.github.io/events.kt/events.kt/relays/publishers) for a complete guide.

##### 3) Create a producer

Create and configure a producer:

```kotlin
val producer = Producer(publisher) {
    serialization { json() }
}
```

You can now publish events:

```kotlin
val event = UserCreatd(user, createdAt = Clock.System.now())
producer.publish(event) { 
    // Metadata
    "traceId" to "trace-123"
}
```

### Creating a consumer

#### 1) Add the dependency to your project

```kotlin
dependencies {
    // Consumer API
    implementation("io.github.briangits.events:consumer:<version>")
    
    // Kafka Consumer
    implementation("io.github.briangits.events:kafka-consumer:<version>")
}
```

#### 2) Create a relay

A relay can be shared by one or more consumers

```kotlin
val relay = KafkaConsumer(brokers = listOf("localhost:9092"), groupId = "my-group") {
    /* Kafka consumer configuration */
}
```

See [creating a relay](https://briangits.github.io/events.kt/events.kt/relays/consumers) for a complete guide.

#### 3) Create a consumer:

Create and configure consumer:

```kotlin
val consumer = Consumer(relay) {
    serialization { json() }
}
```

Subscribe to an event:

```kotlin
consumer.subscribe<UserCreated> { event, metadata ->
    println("User ${event.user.name} created at ${event.createdAt}")
}
```

#### 4) Start the relay

The relay can be started explicitly after all subscriptions have been configured.

This ensures that consumers are fully configured before message processing begins.

```kotlin
relay.start()
```

---

## Supported Brokers

`inetgartion-events` is designed to be broker-agnostic.

The library provides a unified API for publishing and consuming events, 
while allowing the underlying broker implementation to be swapped independently.

Currently supported brokers:
- **[Kafka](https://bringits.github.io/events.kt/relays/kafka)** - Kafka publisher and consumer implementations, 
    currently only supported on JVM & Native targets

---

## Documentation
- **[Getting Started](https://briangits.github.io/events.kt/getting-started)**
- **[Defining Events](https://briangits.github.io/events.kt/defining-events)**
- **[Creating a Producer](https://briangits.github.io/events.kt/producers/introduction)**
- **[Publishing Events](https://briangits.github.io/events.kt/)**
- **[Creating a Consumer](https://briangits.github.io/events.kt/consumers/introduction)**
- **[Consuming Events](https://briangits.github.io/events.kt/consumers/consuming)**
- **[Configuring Serialization](https://briangits.github.io/events.kt/events.kt/serialization)**
- **[Brokers & Relays](https://briangits.github.io/events.kt/relays)**

## License

[Apache-2.0](LICENSE)
