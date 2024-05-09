package no.stackcanary.routes.dto

import java.time.LocalDate

data class Employee(
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val employerId: Int
)

data class Company(
    val name: String,
    val businessArea: String
)

data class Certification(
    val name: String,
    val authority: String,
    val dateEarned: LocalDate,
    val expiryDate: LocalDate,
    val employeeId: Int
)