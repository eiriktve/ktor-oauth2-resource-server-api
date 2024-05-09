package no.stackcanary.dao

import kotlinx.coroutines.Dispatchers
import no.stackcanary.dao.tables.EmployeeTable
import no.stackcanary.routes.dto.Employee
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction

class EmployeeRepository(private val database: Database) {

    // Starts each query in its own coroutine, instead of accessing it in a blocking way
    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(employee: Employee): Int = dbQuery {
        EmployeeTable.insert {
            it[firstName] = employee.firstName
            it[lastName] = employee.lastName
            it[email] = employee.email
            it[position] = employee.position
            it[employerId] = 1
        }[EmployeeTable.employeeId]
    }
}