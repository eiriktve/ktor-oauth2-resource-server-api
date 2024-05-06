# Ktor resource server API 

A ktor API which acts as a resource server in an oauth2.0 client credentials 
grant/flow, protecting resources only accessible by clients presenting a valid access token. 
The token is verified against an authorization server.

Other than that, this repo demonstrates best practices for common use cases, such as:
- Application setup and configuration
- Database setup and configuration (postgres), using JetBrains Exposed
- Authorizing requests  
- Routing and CRUD operations
- OpenAPI documentation

## Domain
The resource guarded by this resource server API is employee information. You can create and change employees, and 
retrieve information about employees and their certifications.

## Database 
The following postgres tables are required. 

````sql
-- Create Employees Table
CREATE TABLE Employees (
    EmployeeID SERIAL PRIMARY KEY,
    FirstName VARCHAR(50),
    LastName VARCHAR(50),
    Department VARCHAR(50),
    Position VARCHAR(50)
);

-- Create Certifications Table
CREATE TABLE Certifications (
    CertificationID SERIAL PRIMARY KEY,
    EmployeeID INT REFERENCES Employees(EmployeeID),
    CertificationName VARCHAR(100),
    CertificationAuthority VARCHAR(100),
    DateEarned DATE,
    ExpiryDate DATE
);
````

## Configuration
Example in-repo yaml config [here](src/main/resources/application.yaml)

## Swagger
Swagger doc is available at **{server}{port}/openapi**