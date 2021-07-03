package com.example.persondata

import com.example.persondata.model.PersonData
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.SocketPolicy
import org.junit.jupiter.api.*
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource(
    properties = [
        "agify.url=http://localhost:8100",
        "genderize.url=http://localhost:8200",
        "nationalize.url=http://localhost:8300",
        "server.port=8400"
    ]
)
internal class PersonDataApplicationMockWebServerTest {

    private val webClient = WebTestClient.bindToServer().baseUrl("http://localhost:8400/person").build()

    @Nested
    inner class MockWebServerTesting {

        private val ageMockWebServer = MockWebServer()
        private val genderMockWebServer = MockWebServer()
        private val nationalMockWebServer = MockWebServer()

        @BeforeEach
        fun setup() {
            ageMockWebServer.start(8100)
            genderMockWebServer.start(8200)
            nationalMockWebServer.start(8300)
        }

        @AfterEach
        fun shutdown() {
            ageMockWebServer.shutdown()
            genderMockWebServer.shutdown()
            nationalMockWebServer.shutdown()
        }

        @Test
        @DisplayName("should be able to get correct response")
        fun testGetPersonData() {
            ageMockWebServer.enqueue(
                MockResponse()
                    .setStatus("HTTP/1.1 " + HttpStatus.OK)
                    //language=JSON
                    .setBody("{\"name\":\"tom\",\"age\":69,\"count\":256874}")
                    .addHeader("Content-Type", "application/json")
            )
            genderMockWebServer.enqueue(
                MockResponse()
                    .setStatus("HTTP/1.1 " + HttpStatus.OK)
                    //language=JSON
                    .setBody("{\"name\":\"tom\",\"gender\":\"male\",\"probability\":0.654,\"count\":2341}")
                    .addHeader("Content-Type", "application/json")
            )
            nationalMockWebServer.enqueue(
                MockResponse()
                    .setStatus("HTTP/1.1 " + HttpStatus.OK)
                    //language=JSON
                    .setBody("{\"name\":\"tom\",\"country\":[{\"country_id\":\"AU\",\"probability\":0.123}]}")
                    .addHeader("Content-Type", "application/json")
            )
            webClient.get()
                .uri("?name=tom")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PersonData::class.java)
        }

        @Test
        @DisplayName("should get GATEWAY_TIMEOUT if API timeout")
        fun testBadGateWayTimeout() {
            ageMockWebServer.enqueue(MockResponse().setSocketPolicy(SocketPolicy.NO_RESPONSE))
            webClient.get()
                .uri("?name=tom")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.GATEWAY_TIMEOUT)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(//language=JSON
                    "{\"error\":\"gateway timeout\"}"
                )
        }

        @Test
        @DisplayName("should get BAD_GATEWAY if API response in bad format")
        fun testBadGateWay() {
            ageMockWebServer.enqueue(
                MockResponse()
                    .setStatus("HTTP/1.1 " + HttpStatus.OK)
                    //language=JSON
                    .setBody("{\"name\":\"tom\",\"age\":\"unexpected\"}")
                    .addHeader("Content-Type", "application/json")
            )
            webClient.get()
                .uri("?name=tom")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.BAD_GATEWAY)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(//language=JSON
                    "{\"error\":\"bad gateway\"}"
                )
        }

        @Test
        @DisplayName("should get service unavailable if API response is not OK")
        fun testGenericBadGateWay() {
            ageMockWebServer.enqueue(
                MockResponse()
                    .setStatus("HTTP/1.1 " + HttpStatus.BAD_REQUEST)
                    .addHeader("Content-Type", "application/json")
            )
            webClient.get()
                .uri("?name=tom")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE)
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .json(//language=JSON
                    "{\"error\":\"service unavailable\"}"
                )
        }
    }
}