package no.stackcanary.di

import no.stackcanary.dao.EmployeeRepository
import no.stackcanary.dao.EmployeeRepositoryImpl
import no.stackcanary.service.EmployeeService
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    singleOf(::EmployeeRepositoryImpl) { bind<EmployeeRepository>() }
    singleOf(::EmployeeService)
}