package no.stackcanary.dao.model

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table

object Company: Table("Company") {
    val companyId: Column<Int> = integer("company_id").autoIncrement()
    val name: Column<Int> = integer("name").uniqueIndex()
    val business: Column<Int> = integer("business")
    override val primaryKey = PrimaryKey(companyId)
}