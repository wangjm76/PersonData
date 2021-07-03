package com.example.persondata.client

import com.example.persondata.model.AgeResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration

@Component
class AgeClient(
    @Value("\${agify.url}") private val url : String
) {
    private val client = WebClient.create(url)

    fun getAge(name : String): Mono<AgeResponse> =
         client.get()
               .uri("/?name=$name")
               .accept(MediaType.APPLICATION_JSON)
               .retrieve()
               .bodyToMono<AgeResponse>()
               .timeout(Duration.ofSeconds(2,0))
               .onErrorMap { HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE) }
}
