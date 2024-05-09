package no.stackcanary.dao.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object EmployeeTable: Table("Employee") {
    val employeeId: Column<Int> = integer("employee_id").autoIncrement()
    val firstName: Column<String> = varchar("first_name", 50)
    val lastName: Column<String> = varchar("last_name", 50)
    val email: Column<String> = varchar("email", 100)
    val position: Column<String> = varchar("position", 50)
    val employerId: Column<Int> = integer("employer_id").references(CompanyTable.companyId)
    override val primaryKey = PrimaryKey(employeeId)
}