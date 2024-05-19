package no.stackcanary.dao.tables

import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.date

object Certifications: Table("Certification") {
    val certificationId: Column<Int> = integer("certification_id").autoIncrement()
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val authority: Column<String> = varchar("authority", 50)
    val dateEarned: Column<LocalDate> = date("date_earned")
    val expiryDate: Column<LocalDate> = date("expiry_date")
    val employeeId: Column<Int> = integer("employee_id").references(Employees.employeeId)
    override val primaryKey = PrimaryKey(certificationId)
}