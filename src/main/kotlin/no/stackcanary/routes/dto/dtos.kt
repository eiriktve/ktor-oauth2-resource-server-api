package no.stackcanary.routes.dto

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val employer: Company,
    val certifications: MutableList<Certification> = mutableListOf()
)

@Serializable
data class Company(
    val id: Int,
    val name: String,
    val businessArea: String
)

@Serializable
data class Certification(
    val id: Int,
    val name: String,
    val authority: String,
    val dateEarned: LocalDate,
    val expiryDate: LocalDate,
)

@Serializable
data class ErrorResponse(
    val status: Int,
    val error: String,
    val message: String,
    val timestamp: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()),
    val path: String? = null
)