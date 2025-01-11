package no.stackcanary.dao

import kotlinx.coroutines.Dispatchers
import no.stackcanary.dao.tables.Certifications
import no.stackcanary.dao.tables.Companies
import no.stackcanary.dao.tables.Employees
import no.stackcanary.routes.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.upsert

interface EmployeeRepository {
    suspend fun upsertEmployee(employee: EmployeeRequest, employeeKey: Int?): Int
    suspend fun fetchEmployeeById(id: Int): EmployeeResponse?
    suspend fun deleteEmployee(id: Int)
    suspend fun createCertification(certificationRequest: CertificationRequest, employeeKey: Int): Int
    suspend fun fetchCertificationsByEmployeeId(id: Int): List<CertificationResponse>
    suspend fun deleteCertificationByEmployeeId(id: Int)
}

class EmployeeRepositoryImpl() : EmployeeRepository {

    /**
     * Utility function used for querying.
     *
     * Starts each query in its own coroutine
     */
    private suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    /**
     * Upsert is achieved using a combination of SQL_ON_CONFLICT or ON_DUPLICATE_KEY_UPDATE (depending on your database)
     * and Exposed’s insert or replace functionality
     */
    override suspend fun upsertEmployee(employee: EmployeeRequest, employeeKey: Int?): Int = dbQuery {
        Employees.upsert(
            onUpdateExclude = listOf(Employees.employeeId, Employees.email)
        ) {
            if (employeeKey != null) it[employeeId] = employeeKey
            it[firstName] = employee.firstName
            it[lastName] = employee.lastName
            it[position] = employee.position
            it[email] = employee.email
            it[employerId] = employee.employerId
        }[Employees.employeeId]
    }

    override suspend fun createCertification(certificationRequest: CertificationRequest, employeeKey: Int): Int = dbQuery {
        Certifications.insert {
            it[name] = certificationRequest.name
            it[authority] = certificationRequest.authority
            it[dateEarned] = certificationRequest.dateEarned
            it[expiryDate] = certificationRequest.expiryDate
            it[employeeId] = employeeKey
        }[Certifications.certificationId]
    }

    override suspend fun fetchEmployeeById(id: Int): EmployeeResponse? {
        return dbQuery {
            (Employees innerJoin Companies)
                .selectAll()
                .where { Employees.employeeId eq id }
                .map {
                    EmployeeResponse(
                        id = it[Employees.employeeId],
                        firstName = it[Employees.firstName],
                        lastName = it[Employees.lastName],
                        email = it[Employees.email],
                        position = it[Employees.position],
                        CompanyResponse(
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

    override suspend fun deleteCertificationByEmployeeId(id: Int) {
        dbQuery { Certifications.deleteWhere { employeeId eq id } }
    }

    override suspend fun fetchCertificationsByEmployeeId(id: Int): List<CertificationResponse> {
        return dbQuery {
            Certifications.selectAll()
                .where { Certifications.employeeId eq id }
                .map {
                    CertificationResponse(
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