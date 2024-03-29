package com.example.persondata.client

import com.example.persondata.model.NationalResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class NationalClient(
    @Value("\${nationalize.url}") private val url: String
) {
    private val client = WebClient.create(url)

    fun getNational(name: String): Mono<NationalResponse> =
        client.get()
            .uri("/?name=$name")
            .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .bodyToMono<NationalResponse>()
            .timeout(Duration.ofSeconds(2))
}