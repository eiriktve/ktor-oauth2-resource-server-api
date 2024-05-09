package no.stackcanary.service

import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.routes.dto.Employee

class EmployeeService(private val repository: EmployeeRepositoryImpl) {

    suspend fun getEmployeeById(id: Int): Employee? = repository.fetchEmployeeById(id)

    suspend fun create(employee: Employee) = repository.createEmployee(employee)

    suspend fun update(id: Int, employee: Employee) = repository.updateEmployee(id, employee)

    suspend fun delete(id: Int) = repository.deleteEmployee(id)

}