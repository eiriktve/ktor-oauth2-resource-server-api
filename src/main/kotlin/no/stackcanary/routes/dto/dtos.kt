package no.stackcanary.routes.dto

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
data class Employee(
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val employerId: Int
)

@Serializable
data class Company(
    val name: String,
    val businessArea: String
)

@Serializable
data class Certification(
    val name: String,
    val authority: String,
    val dateEarned: LocalDate,
    val expiryDate: LocalDate,
    val employeeId: Int
)