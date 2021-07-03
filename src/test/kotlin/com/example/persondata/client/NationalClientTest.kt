package com.example.persondata.client

import com.example.persondata.model.Country
import com.example.persondata.model.NationalResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.test.StepVerifier
import java.util.concurrent.TimeoutException

@ExtendWith(SpringExtension::class)
internal class NationalClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var nationalClient: NationalClient


    @BeforeEach
    fun setup() {
        mockWebServer.start()
        nationalClient = NationalClient(mockWebServer.url("/").toString())
    }

    @AfterEach
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    @DisplayName("national client should make API call to the end point")
    fun testGetNational() {
        mockWebServer.enqueue(
            MockResponse()
                .setStatus("HTTP/1.1 " + HttpStatus.OK)
                //language=JSON
                .setBody("{\"name\":\"tom\",\"country\":[{\"country_id\":\"AU\",\"probability\":0.123}]}")
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(nationalClient.getNational("tom"))
            .expectNext(NationalResponse("tom", listOf(Country("AU", 0.123))))
            .verifyComplete();
    }

    @Test
    @DisplayName("national client should throw exception if API fails")
    fun testAPIFails() {
        mockWebServer.enqueue(
            MockResponse()
                .setStatus("HTTP/1.1 " + HttpStatus.BAD_REQUEST)
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(nationalClient.getNational("tom"))
            .expectError()
            .verify()
    }

    @Test
    @DisplayName("national client should throw exception if API timeout")
    fun testAPITimeout() {
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )
        StepVerifier.create(nationalClient.getNational("tom"))
            .expectError(TimeoutException::class.java)
            .verify()
    }
}