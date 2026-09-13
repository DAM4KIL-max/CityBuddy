package com.adam.citybuddy

data class PRPersona(
    val name: String,
    val role: String,
    val language: String,
    val image: String,
    val email: String,
    val phrases: List<String>,
    val keywords: List<String>
)