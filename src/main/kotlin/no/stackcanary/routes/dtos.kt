package no.stackcanary.routes

import kotlinx.datetime.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrUpdateEmployeeRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val employerId: Int,
    val certifications: List<CertificationRequest> = mutableListOf()
)

@Serializable
data class CertificationRequest(
    val name: String,
    val authority: String,
    val dateEarned: LocalDate,
    val expiryDate: LocalDate,
)

@Serializable
data class EmployeeResponse(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val position: String,
    val employer: CompanyResponse,
    val certifications: MutableList<CertificationResponse>? = mutableListOf()
)

@Serializable
data class CompanyResponse(
    val id: Int,
    val name: String,
    val businessArea: String
)

@Serializable
data class CertificationResponse(
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