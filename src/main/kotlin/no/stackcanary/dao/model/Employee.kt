package no.stackcanary.dao.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Employee: Table("Employee") {
    val employeeId: Column<Int> = integer("employee_id").autoIncrement()
    val firstName: Column<String> = varchar("first_name", 50)
    val lastName: Column<String> = varchar("last_name", 50)
    val position: Column<String> = varchar("position", 50)
    val employerID: Column<Int> = integer("employer_id").references(Company.companyId)
    override val primaryKey = PrimaryKey(employeeId)
}