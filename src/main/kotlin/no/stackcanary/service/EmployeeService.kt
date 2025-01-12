package no.stackcanary.service

import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.routes.EmployeeRequest
import no.stackcanary.routes.EmployeeResponse
import no.stackcanary.routes.IdResponse

class EmployeeService(private val repository: EmployeeRepositoryImpl) {

    suspend fun getEmployeeById(employeeKey: Int): EmployeeResponse? =
        repository.fetchEmployeeById(employeeKey)
            .also { it?.certifications?.addAll(repository.fetchCertificationsByEmployeeId(employeeKey)) }

    /**
     * @param employeeKey null if this is a new employee, non-null if this is an update
     */
    suspend fun upsertEmployee(employee: EmployeeRequest, employeeKey: Int?): IdResponse {
        val employeeId: Int = repository.upsertEmployee(employee, employeeKey)
        employee.certifications.forEach { repository.createCertification(it, employeeId) }
        return IdResponse(id = employeeId, message = if (employeeKey == null) "Employee created" else "Employee updated")
    }

    suspend fun updateEmployee(employee: EmployeeRequest, employeeKey: Int): EmployeeResponse? {
        upsertEmployee(employee, employeeKey)
        repository.deleteCertificationByEmployeeId(employeeKey)
        employee.certifications.forEach { repository.createCertification(it, employeeKey) }
        return getEmployeeById(employeeKey)
    }

    suspend fun delete(employeeKey: Int): IdResponse {
        repository.deleteCertificationByEmployeeId(employeeKey)
        repository.deleteEmployee(employeeKey)
        return IdResponse(id = employeeKey, message = "Employee deleted")
    }
}