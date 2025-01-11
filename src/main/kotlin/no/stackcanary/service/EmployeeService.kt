package no.stackcanary.service

import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.routes.EmployeeRequest
import no.stackcanary.routes.EmployeeResponse

class EmployeeService(private val repository: EmployeeRepositoryImpl) {

    suspend fun getEmployeeById(id: Int): EmployeeResponse? =
        repository.fetchEmployeeById(id)
            .also { it?.certifications?.addAll(repository.fetchCertificationsByEmployeeId(id)) }

    /**
     * @param employeeKey null if this is a new employee, non-null if this is an update
     */
    suspend fun upsertEmployee(employee: EmployeeRequest, employeeKey: Int?): Int {
        val employeeId: Int = repository.upsertEmployee(employee, employeeKey)
        employee.certifications.forEach { repository.createCertification(it, employeeId) }
        return employeeId
    }

    suspend fun updateEmployee(employee: EmployeeRequest, employeeKey: Int): EmployeeResponse? {
        upsertEmployee(employee, employeeKey)
        repository.deleteCertificationByEmployeeId(employeeKey)
        employee.certifications.forEach { repository.createCertification(it, employeeKey) }
        return getEmployeeById(employeeKey)
    }

    suspend fun delete(id: Int) {
        repository.deleteCertificationByEmployeeId(id)
        repository.deleteEmployee(id)
    }
}