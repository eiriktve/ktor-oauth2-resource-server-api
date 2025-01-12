# Ktor resource server API 

A ktor API which acts as a resource server in an oauth2.0 client credentials 
grant/flow, protecting resources only accessible by clients presenting a valid access token. 
The token is verified against an authorization server.

The repo explores best practices for common use cases, such as:
- Application setup and configuration
- Database setup and configuration (postgres), using JetBrains Exposed
- Authorizing requests by integrating with an **Oauth2 Authorization Server**  
- Routing and CRUD operations
- Dependency Injection with [Koin](https://insert-koin.io)
- OpenAPI documentation

Some light reading on the responsibilities of a resource server: https://www.oauth.com/oauth2-servers/the-resource-server/

## Technologies
- kotlin 2.x with JVM 21
- ktor 2.x
- koin
- exposed

## Domain
The resource guarded by this resource server API is employee information. You can create and change employees, and 
retrieve information about employees and their certifications.

## Configuration
The application is a self-contained package (as opposed to a deployable WAR) with netty as the application engine,
and with an (external) server configuration file.

Example in-repo yaml config [here](src/main/resources/application.yaml)

## Database
The following postgres tables are required.

````sql
-- Create Company Table
CREATE TABLE Company (
    company_id SERIAL PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL,
    business VARCHAR(50) NOT NULL -- i.e., IT, Health, Stocks/trading
);

-- Create Employee Table
CREATE TABLE Employee (
    employee_id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    position VARCHAR(50),
    employer_id INT REFERENCES Company(company_id)
);

-- Create Certification Table
CREATE TABLE Certification (
    certification_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    authority VARCHAR(100) NOT NULL,
    date_earned DATE NOT NULL,
    expiry_date DATE NOT NULL,
    employee_id INT REFERENCES Employee(employee_id)
);
````

## Swagger
Swagger doc is available at **{server}{port}/openapi**

## Postman examples
There's a postman collection located [here](/postman). As for the Authorization header, you need to get a token from the
configured Authorization Server. If you want to set it up yourself, I have one 
[here](https://github.com/eiriktve/kotlin-spring-oauth2-authorization-server) you can clone. 