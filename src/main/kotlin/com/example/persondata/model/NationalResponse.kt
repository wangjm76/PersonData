package com.example.persondata.model

data class Country(val country_id: String, val probability: Double)
data class NationalResponse(val name: String, val country: List<Country>)
