package adam.bickford.springkotlinwebfluxapp

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.kotlin.core.util.function.component1
import reactor.kotlin.core.util.function.component2
import reactor.util.function.Tuples
import java.time.Duration
import java.time.Instant

@SpringBootApplication
@RestController
class SpringKotlinWebfluxAppApplication {

    @Value("\${downstream.service:_}")
    private lateinit var downstreamService: String

    @Value("\${service.name}")
    private lateinit var serviceName: String

    private val webClient = WebClient.builder().build()

    @GetMapping("/request")
    fun request(@RequestParam("latencies") latencies: List<Int>): Mono<List<BlockingResponse>> {
        val delay = latencies[0]
        val callDownstream = latencies.size > 1 && downstreamService != "_"
        return Mono.just(Instant.now())
            .delayElement(Duration.ofMillis(delay.toLong()))
            .flatMap { start ->
                getDownstreamResults(callDownstream, latencies)
                    .flatMap { downstreamResults ->
                        getMetrics(start, delay)
                            .map { downstreamResults + it }
                    }
            }
    }

    @GetMapping("/request2")
    fun request2(@RequestParam("latencies") latencies: List<Int>): Mono<List<BlockingResponse>> {
        val delay = latencies[0]
        val callDownstream = latencies.size > 1 && downstreamService != "_"
        return Mono.just(Instant.now())
            .delayElement(Duration.ofMillis(delay.toLong()))
            .flatMap { start ->
                getDownstreamResults(callDownstream, latencies)
                    .map { Tuples.of(start, it) }
            }
            .flatMap { (start, results) ->
                getMetrics(start, delay)
                    .map { results + it }
            }
    }

    private fun getDownstreamResults(callDownstream: Boolean, latencies: List<Int>) =
        when {
            callDownstream -> fetchDownstreamResults(latencies)
            else -> Mono.just(emptyList())
        }


    private fun getMetrics(start: Instant, delay: Int): Mono<List<BlockingResponse>> =
        Instant.now().let {
            listOf(
                BlockingResponse(
                    serviceName = serviceName,
                    delay = delay,
                    actual = (it.toEpochMilli() - start.toEpochMilli()).toInt(),
                    requestTime = start,
                    responseTime = it
                )
            )
        }
            .toMono()

    private fun fetchDownstreamResults(latencies: List<Int>): Mono<List<BlockingResponse>> =
        webClient.get()
            .uri(downstreamService + "/request?latencies=" + latencies.joinToString(","))
            .retrieve()
            .bodyToMono()
}

data class BlockingResponse(
    val serviceName: String,
    val delay: Int,
    val actual: Int,
    val requestTime: Instant,
    val responseTime: Instant
)

fun main(args: Array<String>) {
    runApplication<SpringKotlinWebfluxAppApplication>(*args)
}
