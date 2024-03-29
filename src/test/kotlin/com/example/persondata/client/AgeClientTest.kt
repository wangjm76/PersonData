package com.example.persondata.client

import com.example.persondata.model.AgeResponse
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
internal class AgeClientTest {

    private val mockWebServer = MockWebServer()
    private lateinit var ageClient: AgeClient


    @BeforeEach
    fun setup() {
        mockWebServer.start()
        ageClient = AgeClient(mockWebServer.url("/").toString())
    }

    @AfterEach
    fun shutdown() {
        mockWebServer.shutdown()
    }

    @Test
    @DisplayName("age client should make API call to the end point")
    fun testGetAge() {
        mockWebServer.enqueue(
            MockResponse()
                .setStatus("HTTP/1.1 " + HttpStatus.OK)
                //language=JSON
                .setBody("{\"name\":\"tom\",\"age\":69,\"count\":256874}")
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(ageClient.getAge("tom"))
            .expectNext(AgeResponse("tom", 69, 256874))
            .verifyComplete();
    }

    @Test
    @DisplayName("age client should throw exception if API fails")
    fun testAPIFails() {
        mockWebServer.enqueue(
            MockResponse()
                .setStatus("HTTP/1.1 " + HttpStatus.BAD_REQUEST)
                .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(ageClient.getAge("tom"))
            .expectError()
            .verify()
    }

    @Test
    @DisplayName("age client should throw exception if API timeout")
    fun testAPITimeout() {
        mockWebServer.enqueue(
            MockResponse()
                .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )
        StepVerifier.create(ageClient.getAge("tom"))
            .expectError(TimeoutException::class.java)
            .verify()
    }
}