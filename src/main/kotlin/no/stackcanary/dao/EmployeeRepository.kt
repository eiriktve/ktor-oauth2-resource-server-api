package no.stackcanary.dao

import kotlinx.coroutines.Dispatchers
import no.stackcanary.dao.tables.Employees
import no.stackcanary.routes.dto.Employee
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

interface EmployeeRepository {
    suspend fun createEmployee(employee: Employee): Int
    suspend fun updateEmployee(id: Int, employee: Employee): Unit
    suspend fun fetchEmployeeById(id: Int): Employee?
    suspend fun deleteEmployee(id: Int)
}

class EmployeeRepositoryImpl(): EmployeeRepository {

    /**
     * Utility function used for querying.
     *
     * Starts each query in its own coroutine, instead of accessing it in a blocking way.
     */
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    override suspend fun createEmployee(employee: Employee): Int = dbQuery {
        Employees.insert {
            it[firstName] = employee.firstName
            it[lastName] = employee.lastName
            it[email] = employee.email
            it[position] = employee.position
            it[employerId] = 1 // TODO
        }[Employees.employeeId]
    }

    /**
     * @param id ID of the employee to update
     * @param employee Updated values
     */
    override suspend fun updateEmployee(id: Int, employee: Employee) {
        dbQuery {
            Employees.update({ Employees.employeeId eq id }) {
                it[firstName] = employee.firstName
                it[lastName] = employee.lastName
                it[email] = employee.email
                it[position] = employee.position
                it[employerId] = 1 // TODO
            }
        }
    }

    override suspend fun fetchEmployeeById(id: Int): Employee? {
        return dbQuery {
            Employees.selectAll()
                .where { Employees.employeeId eq id }
                .map { Employee(
                    firstName = it[Employees.firstName],
                    lastName = it[Employees.lastName],
                    email = it[Employees.email],
                    position = it[Employees.position],
                    employerId = it[Employees.employerId])
                }.singleOrNull()
        }
    }

    override suspend fun deleteEmployee(id: Int) {
        dbQuery { Employees.deleteWhere { employeeId eq id } }
    }
}