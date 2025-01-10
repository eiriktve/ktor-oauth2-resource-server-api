package no.stackcanary.service

import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.routes.CreateEmployeeRequest
import no.stackcanary.routes.EmployeeResponse

class EmployeeService(private val repository: EmployeeRepositoryImpl) {

    suspend fun getEmployeeById(id: Int): EmployeeResponse? =
        repository.fetchEmployeeById(id)
            .also { it?.certifications?.addAll(repository.fetchCertificationsByEmployeeId(id)) }


    suspend fun createEmployee(employee: CreateEmployeeRequest) {
        val employeeId: Int = repository.createEmployee(employee)
        employee.certifications.forEach { repository.createCertification(it, employeeId) }
    }

    suspend fun update(id: Int, employee: CreateEmployeeRequest) =
        repository.updateEmployee(id, employee)

    suspend fun delete(id: Int) {
        repository.deleteCertificationByEmployeeId(id)
        repository.deleteEmployee(id)
    }
}