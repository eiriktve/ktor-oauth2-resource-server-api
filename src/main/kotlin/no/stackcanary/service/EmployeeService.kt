package no.stackcanary.service

import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.routes.dto.Employee

class EmployeeService(private val repository: EmployeeRepositoryImpl) {

    suspend fun getEmployeeById(id: Int): Employee? {
        val employee = repository.fetchEmployeeById(id)
        employee?.certifications?.addAll(repository.getCertificationsByEmployeeId(employee.id))
        return employee
    }

    suspend fun create(employee: Employee) = repository.createEmployee(employee)

    suspend fun update(id: Int, employee: Employee) = repository.updateEmployee(id, employee)

    suspend fun delete(id: Int) = repository.deleteEmployee(id)

}