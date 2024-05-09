package no.stackcanary.dao.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.date
import java.time.LocalDate

object Certification: Table("Certification") {
    private val certificationId: Column<Int> = integer("certification_id").autoIncrement()
    val name: Column<Int> = integer("name").uniqueIndex()
    val authority: Column<Int> = integer("authority")
    val dateEarned: Column<LocalDate> = date("date_earned")
    val expiryDate: Column<LocalDate> = date("expiry_date")
    val employeeId: Column<Int> = integer("EmployeeID").references(Employee.employeeId)
    override val primaryKey = PrimaryKey(certificationId)
}