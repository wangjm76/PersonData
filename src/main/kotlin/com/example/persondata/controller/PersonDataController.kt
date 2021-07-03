package com.example.persondata.controller

import com.example.persondata.service.PersonDataService
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebInputException
import java.util.concurrent.TimeoutException

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

    @ExceptionHandler(DecodingException::class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    fun badGateWay() = mapOf("error" to "bad gateway")

    @ExceptionHandler(TimeoutException::class)
    @ResponseStatus(HttpStatus.GATEWAY_TIMEOUT)
    fun gatewayTimeOut() = mapOf("error" to "gateway timeout")

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    fun serviceUnavailable() = mapOf("error" to "service unavailable")
}