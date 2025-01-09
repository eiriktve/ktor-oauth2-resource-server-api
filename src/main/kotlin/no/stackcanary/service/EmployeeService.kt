package no.stackcanary.service

import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.routes.CreateOrUpdateEmployeeRequest
import no.stackcanary.routes.EmployeeResponse

class EmployeeService(private val repository: EmployeeRepositoryImpl) {

    suspend fun getEmployeeById(id: Int): EmployeeResponse? {
        val employee = repository.fetchEmployeeById(id)
        employee?.certifications?.addAll(repository.fetchCertificationsByEmployeeId(id))
        return employee
    }

    suspend fun createEmployee(employee: CreateOrUpdateEmployeeRequest) {
        val id: Int = repository.createEmployee(employee)
        employee.certifications.forEach { repository.createCertification(it, id) }
    }

    suspend fun update(id: Int, employee: CreateOrUpdateEmployeeRequest) = repository.updateEmployee(id, employee)

    suspend fun delete(id: Int) = repository.deleteEmployee(id)

}