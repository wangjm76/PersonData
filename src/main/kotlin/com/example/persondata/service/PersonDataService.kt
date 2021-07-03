package com.example.persondata.service

import com.example.persondata.client.AgeClient
import com.example.persondata.client.GenderClient
import com.example.persondata.client.NationalClient
import com.example.persondata.model.PersonData
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Service
class PersonDataService(
    private val ageClient: AgeClient,
    private val genderClient: GenderClient,
    private val nationalClient: NationalClient,
) {
    fun getPersonData(name : String) : Mono<PersonData> =
        Mono.zip(ageClient.getAge(name).subscribeOn(Schedulers.parallel()),
                 genderClient.getGender(name).subscribeOn(Schedulers.parallel()),
                 nationalClient.getNational(name).subscribeOn(Schedulers.parallel()))
            .map {
                tuple -> PersonData(tuple.t1.age,tuple.t2.gender,
                     tuple.t3.country.maxWithOrNull
                     { c1, c2 -> c1.probability.compareTo(c2.probability) }?.country_id ?: "UNKNOWN")
           }
}