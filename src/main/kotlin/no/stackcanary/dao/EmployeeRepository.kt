package no.stackcanary.dao

import kotlinx.coroutines.Dispatchers
import no.stackcanary.dao.tables.Certifications
import no.stackcanary.dao.tables.Companies
import no.stackcanary.dao.tables.Employees
import no.stackcanary.routes.dto.Certification
import no.stackcanary.routes.dto.Company
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
    suspend fun getCertificationsByEmployeeId(id: Int): List<Certification>
}

class EmployeeRepositoryImpl() : EmployeeRepository {

    /**
     * Utility function used for querying.
     *
     * Starts each query in its own coroutine
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
                it[employerId] = 1
            }
        }
    }

    override suspend fun fetchEmployeeById(id: Int): Employee? {
        return dbQuery {
            (Employees innerJoin Companies)
                .selectAll()
                .where { Employees.employeeId eq id }
                .map {
                    Employee(
                        id = it[Employees.employeeId],
                        firstName = it[Employees.firstName],
                        lastName = it[Employees.lastName],
                        email = it[Employees.email],
                        position = it[Employees.position],
                        Company(
                            id = it[Companies.companyId],
                            name = it[Companies.name],
                            businessArea = it[Companies.business]
                        )
                    )
                }
        }.singleOrNull()
    }

    override suspend fun deleteEmployee(id: Int) {
        dbQuery { Employees.deleteWhere { employeeId eq id } }
    }

    override suspend fun getCertificationsByEmployeeId(id: Int): List<Certification> {
        return dbQuery {
            Certifications.selectAll()
                .where { Certifications.employeeId eq id }
                .map {
                    Certification(
                        id = it[Certifications.certificationId],
                        name = it[Certifications.name],
                        authority = it[Certifications.authority],
                        dateEarned = it[Certifications.dateEarned],
                        expiryDate = it[Certifications.expiryDate],
                    )
                }
        }
    }
}