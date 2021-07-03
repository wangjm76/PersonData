package com.example.persondata.client

import com.example.persondata.model.GenderResponse
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.*
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpStatus
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.client.HttpClientErrorException
import reactor.test.StepVerifier

@ExtendWith(SpringExtension::class)
internal class GenderClientTest{

    private val mockWebServer = MockWebServer()
    private lateinit var genderClient: GenderClient


    @BeforeEach
    fun setup(){
        mockWebServer.start()
        genderClient = GenderClient(mockWebServer.url("/").toString())
    }

    @Test
    @DisplayName("gender client should make API call to the end point")
    fun testGetGender() {
        mockWebServer.enqueue(
            MockResponse()
            .setStatus("HTTP/1.1 " + HttpStatus.OK)
                //language=JSON
            .setBody("{\"name\":\"tom\",\"gender\":\"male\",\"probability\":0.654,\"count\":2341}")
            .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(genderClient.getGender("tom"))
            .expectNext(GenderResponse("tom","male",0.654,2341))
            .verifyComplete();
    }

    @Test
    @DisplayName("gender client should throw exception if API fails")
    fun testAPIFails() {
        mockWebServer.enqueue(
            MockResponse()
            .setStatus("HTTP/1.1 " + HttpStatus.BAD_REQUEST)
            .addHeader("Content-Type", "application/json")
        )
        StepVerifier.create(genderClient.getGender("tom"))
            .expectError(HttpClientErrorException::class.java)
            .verify()
    }

    @Test
    @DisplayName("gender client should throw exception if API timeout")
    fun testAPITimeout() {
        mockWebServer.enqueue(
            MockResponse()
            .setSocketPolicy(SocketPolicy.NO_RESPONSE)
        )
        StepVerifier.create(genderClient.getGender("tom"))
            .expectError(HttpClientErrorException::class.java)
            .verify()
    }
}