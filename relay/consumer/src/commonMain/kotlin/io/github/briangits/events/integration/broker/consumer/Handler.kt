package io.github.briangits.events.integration.broker.consumer

import io.github.briangits.events.integration.broker.Message

typealias Handler = suspend (message: Message) -> Unit