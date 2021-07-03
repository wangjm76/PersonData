package com.example.persondata.controller

import com.example.persondata.model.PersonData
import com.example.persondata.service.PersonDataService
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.web.client.HttpClientErrorException
import reactor.core.publisher.Mono

@ExtendWith(SpringExtension::class)
@WebFluxTest(controllers = [PersonDataController::class])
internal class PersonDataControllerTest {

    @MockBean
    private lateinit var personDataService: PersonDataService

    @Autowired
    private val webClient: WebTestClient = WebTestClient.bindToController(PersonDataController::class).build()


    @Test
    @DisplayName("test get data successfully")
    fun testGetPersonData() {
        val tom = PersonData(50,"male","AU")
        Mockito.`when`(personDataService.getPersonData("tom")).thenReturn(Mono.just(tom))
        webClient.get()
            .uri("/person?name=tom")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectBody(PersonData::class.java)

        verify(personDataService, times(1)).getPersonData("tom")
    }

    @Test
    @DisplayName("test getting service unavailable when API call fails")
    fun testServiceNotAvailable() {
        Mockito.`when`(personDataService.getPersonData("tom")).thenThrow(HttpClientErrorException(HttpStatus.SERVICE_UNAVAILABLE))
        webClient.get()
            .uri("/person?name=tom")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError

        verify(personDataService, times(1)).getPersonData("tom")
    }

    @Test
    @DisplayName("test getting a bad request if name is not provided")
    fun testWhenNameIsMissing() {
        webClient.get()
            .uri("/person")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest

        verifyNoMoreInteractions(personDataService)
    }
}