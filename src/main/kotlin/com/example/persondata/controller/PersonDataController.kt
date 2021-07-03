package com.example.persondata.controller

import com.example.persondata.service.PersonDataService
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.server.ServerWebInputException

@RestController
@RequestMapping("/person")
class PersonDataController(
    val personDataService: PersonDataService
) {
    @GetMapping(produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getPersonData(@RequestParam name: String) = personDataService.getPersonData(name)

    @ExceptionHandler(ServerWebInputException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun missingRequestParameter() = mapOf("error" to "name is required")

    @ExceptionHandler(HttpClientErrorException::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun serviceUnavailable() = mapOf("error" to "service unavailable")
}