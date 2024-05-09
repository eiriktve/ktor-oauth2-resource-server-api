package no.stackcanary.dao.tables

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Companies: Table("Company") {
    val companyId: Column<Int> = integer("company_id").autoIncrement()
    val name: Column<String> = varchar("name", 50).uniqueIndex()
    val business: Column<String> = varchar("business", 50)
    override val primaryKey = PrimaryKey(companyId)
}