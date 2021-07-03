package com.example.persondata

import com.example.persondata.model.PersonData
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
internal class PersonDataApplicationTest{

    private val webClient =  WebTestClient.bindToServer().baseUrl("http://localhost:8090/person").build()

    @Test
    @DisplayName("should be able to get correct response")
    fun testGetPersonData() {
            webClient.get()
            .uri("?name=tom")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody(PersonData::class.java)
    }

    @Test
    @DisplayName("should get service unavailable with name with digit")
    fun testGetAnotherPersonData() {
        webClient.get()
            .uri("?name=f123")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
    }

    @Test
    @DisplayName("should get bad request when name not provided")
    fun testGetNoNamePersonData() {
        webClient.get()
            .uri("?test")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
    }

    @Test
    @DisplayName("should be able to get correct response for each call of names")
    fun testGetListOfNamesPersonData() {
        listOf("tom","michael","Sasa","mike","ying","chen","Wosaka").forEach {
            webClient.get()
                .uri("?name=$it")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBody(PersonData::class.java)
        }
    }
}