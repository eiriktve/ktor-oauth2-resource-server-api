package no.stackcanary.dao.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object CertificationTable: Table("Certification") {
    private val certificationId: Column<Int> = integer("certification_id").autoIncrement()
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val authority: Column<String> = varchar("authority", 50)
    val dateEarned: Column<LocalDate> = date("date_earned")
    val expiryDate: Column<LocalDate> = date("expiry_date")
    val employeeId: Column<Int> = integer("EmployeeID").references(EmployeeTable.employeeId)
    override val primaryKey = PrimaryKey(certificationId)
}