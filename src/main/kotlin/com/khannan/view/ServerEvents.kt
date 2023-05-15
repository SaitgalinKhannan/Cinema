/*
package com.khannan.view

import com.khannan.model.SseEvent
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.broadcast
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.delay

suspend fun ApplicationCall.respondSse(events: ReceiveChannel<SseEvent>) {
    response.cacheControl(CacheControl.NoCache(null))
    respondTextWriter(contentType = ContentType.Text.EventStream) {
        for (event in events) {
            if (event.id != null) {
                write("id: ${event.id}\n")
            }
            if (event.event != null) {
                write("event: ${event.event}\n")
            }
            for (dataLine in event.data.lines()) {
                write("data: $dataLine\n")
            }
            write("\n")
            flush()
        }
    }
}

@Suppress("Unused")
@OptIn(ExperimentalCoroutinesApi::class, ObsoleteCoroutinesApi::class)
fun Application.configureSSE() {
    */
/**
     * We produce a [BroadcastChannel] from a suspending function
     * that send a [SseEvent] instance each second.
     *//*

    val channel = produce { // this: ProducerScope<SseEvent> ->
        var n = 0
        while (true) {
            send(SseEvent("demo$n"))
            delay(1000)
            n++
        }
    }.broadcast()

    */
/**
     * We use the [Routing] plugin to declare [Route] that will be
     * executed per call
     *//*

    routing {
        */
/**
         * Route to be executed when the client perform a GET `/sse` request.
         * It will respond using the [respondSse] extension method defined in this same file
         * that uses the [BroadcastChannel] channel we created earlier to emit those events.
         *//*

        get("/sse") {
            val events = channel.openSubscription()
            try {
                call.respondSse(events)
            } finally {
                events.cancel()
            }
        }
        */
/**
         * Route to be executed when the client perform a GET `/` request.
         * It will serve instead HTML file embedded directly in this string that
         * contains JavaScript code to connect to the `/sse` endpoint using
         * the EventSource JavaScript class ( https://html.spec.whatwg.org/multipage/comms.html#the-eventsource-interface ).
         * Normally you would serve HTML and JS files using the [static] method.
         * But for illustrative reasons we are embedding this here.
         *//*

        get("/") {
            call.respondText(
                """
                        <html>
                            <head></head>
                            <body>
                                <ul id="events">
                                </ul>
                                <script type="text/javascript">
                                    var source = new EventSource('/sse');
                                    var eventsUl = document.getElementById('events');
                                    function logEvent(text) {
                                        var li = document.createElement('li')
                                        li.innerText = text;
                                        eventsUl.appendChild(li);
                                    }
                                    source.addEventListener('message', function(e) {
                                        logEvent('message:' + e.data);
                                    }, false);
                                    source.addEventListener('open', function(e) {
                                        logEvent('open');
                                    }, false);
                                    source.addEventListener('error', function(e) {
                                        if (e.readyState == EventSource.CLOSED) {
                                            logEvent('closed');
                                        } else {
                                            logEvent('error');
                                            console.log(e);
                                        }
                                    }, false);
                                </script>
                            </body>
                        </html>
                    """.trimIndent(),
                contentType = ContentType.Text.Html
            )
        }
    }
}*/
