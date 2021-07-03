package com.example.persondata.service

import com.example.persondata.client.AgeClient
import com.example.persondata.client.GenderClient
import com.example.persondata.client.NationalClient
import com.example.persondata.model.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
internal class PersonDataServiceTest{
    @Mock
    private lateinit var ageClient: AgeClient

    @Mock
    private lateinit var genderClient : GenderClient

    @Mock
    private lateinit var nationalClient : NationalClient

    @InjectMocks
    private lateinit var personDataService : PersonDataService

    @Test
    @DisplayName("test able to get data from all clients and merge them to a Mono")
    fun testGetPersonData() {
        Mockito.`when`(ageClient.getAge("tom")).thenReturn(Mono.just(AgeResponse("tom",25,1)))
        Mockito.`when`(genderClient.getGender("tom")).thenReturn(Mono.just(GenderResponse("tom","male",0.123,3)))
        Mockito.`when`(nationalClient.getNational("tom")).thenReturn(Mono.just(NationalResponse("tom",listOf(Country("AU",0.865)))))
        StepVerifier.create(personDataService.getPersonData("tom")).expectNext(PersonData(25,"male","AU"))
            .verifyComplete()
        Mockito.verify(ageClient, Mockito.times(1)).getAge("tom")
        Mockito.verify(genderClient, Mockito.times(1)).getGender("tom")
        Mockito.verify(nationalClient, Mockito.times(1)).getNational("tom")
    }

    @Test
    @DisplayName("test country set to UNKNOWN when no country list return from API")
    fun testEmptyCountryList() {
        Mockito.`when`(ageClient.getAge("tom")).thenReturn(Mono.just(AgeResponse("tom",25,1)))
        Mockito.`when`(genderClient.getGender("tom")).thenReturn(Mono.just(GenderResponse("tom","male",0.123,3)))
        Mockito.`when`(nationalClient.getNational("tom")).thenReturn(Mono.just(NationalResponse("tom", emptyList())))
        StepVerifier.create(personDataService.getPersonData("tom")).expectNext(PersonData(25,"male","UNKNOWN"))
            .verifyComplete()
        Mockito.verify(ageClient, Mockito.times(1)).getAge("tom")
        Mockito.verify(genderClient, Mockito.times(1)).getGender("tom")
        Mockito.verify(nationalClient, Mockito.times(1)).getNational("tom")
    }

    @Test
    @DisplayName("test country set to the one with highest probability when multiple countries are returned")
    fun testMultipleCountries() {
        Mockito.`when`(ageClient.getAge("tom")).thenReturn(Mono.just(AgeResponse("tom",25,1)))
        Mockito.`when`(genderClient.getGender("tom")).thenReturn(Mono.just(GenderResponse("tom","male",0.123,3)))
        Mockito.`when`(nationalClient.getNational("tom")).thenReturn(Mono.just(NationalResponse("tom", listOf(Country("AU",0.865),Country("US",0.345)))))
        StepVerifier.create(personDataService.getPersonData("tom")).expectNext(PersonData(25,"male","AU"))
            .verifyComplete()
        Mockito.verify(ageClient, Mockito.times(1)).getAge("tom")
        Mockito.verify(genderClient, Mockito.times(1)).getGender("tom")
        Mockito.verify(nationalClient, Mockito.times(1)).getNational("tom")
    }
}