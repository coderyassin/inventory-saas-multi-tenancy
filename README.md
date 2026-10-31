# Inventory SaaS

A multi-tenant inventory management application built with Spring Boot . 
The project is designed around **schema-per-tenant
isolation**, where each organization works within its own PostgreSQL
schema.

> **Branch:** `feature/multi-tenant-schema-per-tenant`

## Table of contents

-   [Overview](#overview)
-   [Architecture and tenancy model](#architecture-and-tenancy-model)
-   [Technology stack](#technology-stack)
-   [Prerequisites](#prerequisites)
-   [Getting started](#getting-started)
-   [Configuration](#configuration)
-   [Database migrations](#database-migrations)
-   [Running the application](#running-the-application)
-   [API documentation](#api-documentation)
-   [Testing](#testing)
-   [Deployment](#deployment)
-   [Security notes](#security-notes)
-   [Project status and contribution](#project-status-and-contribution)

## Overview

Inventory SaaS is a multi-tenant inventory management application. Its
tenancy model separates organizations at the database-schema level,
while allowing the application to run as a shared application instance.

The intended isolation model is:

-   One PostgreSQL database.
-   A separate PostgreSQL schema for each organization/tenant.
-   A user belongs to one organization.
-   Tenant-specific data is accessed through the schema associated with
    the authenticated user's organization.

The application is developed as a modular application rather than
requiring a separately deployed service for every business capability.

## Architecture and tenancy model

### Schema per tenant

Each tenant has a dedicated schema in PostgreSQL. This provides logical
separation of tenant data while keeping database operations and
infrastructure centralized.

Conceptually:

``` text
PostgreSQL database
│
├── public / shared schema
│   └── shared application data (where applicable)
│
├── tenant_<organization_1>
│   └── tenant-specific tables and data
│
├── tenant_<organization_2>
│   └── tenant-specific tables and data
│
└── tenant_<organization_n>
    └── tenant-specific tables and data
```

The authenticated user's organization determines the tenant context used
for tenant-scoped operations. Tenant context must be established and
cleared safely for every request so that data from one organization
cannot be accessed from another.

> The diagram describes the intended schema-per-tenant model. Consult
> the implementation in this branch for the exact shared-schema tables,
> schema naming convention, and tenant-resolution flow.

## Technology stack

The repository configuration and project implementation include the
following technologies:

  Area                               Technology
  ---------------------------------- --------------------------------
  Backend                            Java 25, Spring Boot 4.1.2
  API                                Spring MVC / REST
  Persistence                        Spring Data JPA, Hibernate
  Database                           PostgreSQL
  Database migrations                Flyway
  Authentication and authorization   Spring Security, JWT
  JWT library                        JJWT
  API documentation                  Springdoc OpenAPI / Swagger UI
  Build                              Maven
  Backend logging                    Spring Boot logging / Logback
  Local infrastructure               Docker Compose

Check the Maven for the exact version
used by the checked-out branch.

## Prerequisites

Install the tools required by the components you want to run:

-   Java 25 JDK
-   Maven (or use the Maven Wrapper if it is present in the repository)
-   PostgreSQL, or Docker Engine with the Docker Compose plugin

Verify the Java installation:

``` bash
java -version
```

Verify Maven:

``` bash
mvn -version
```

Verify Docker Compose if you use the containerized database:

``` bash
docker compose version
```

## Getting started

### 1. Clone the repository and select the feature branch

``` bash
git clone https://github.com/coderyassin/inventory-saas-multi-tenancy.git
cd inventory-saas-multi-tenancy
git checkout feature/multi-tenant-schema-per-tenant
```

### 2. Configure environment variables

The project uses environment-specific configuration files:

-   `.env.docker` --- values used by Docker Compose.
-   `.env.spring` --- values used by the Spring Boot application.

Create local copies from the examples if the repository provides them.
Otherwise, create the files using the variable names referenced by
`application.yml` and `docker-compose.yml`.

**Do not commit real credentials, tokens, private keys, or production
configuration files.**

Spring Boot imports the environment files as properties when configured
in `application.yml`. Docker Compose does not automatically use an
arbitrarily named `.env.docker` file; pass it explicitly:

``` bash
docker compose --env-file .env.docker up -d
```

### 3. Start PostgreSQL

If the repository's Compose configuration defines the PostgreSQL
service:

``` bash
docker compose --env-file .env.docker up -d
```

Check the running containers:

``` bash
docker compose ps
```

If PostgreSQL is managed outside Docker, configure the database
connection properties in `.env.spring` instead.

### 4. Build the backend

From the repository root:

``` bash
mvn clean verify
```

The generated JAR is normally placed in `target/`. The exact filename
follows the Maven `artifactId` and `version` in `pom.xml`.

## Configuration

The backend reads its runtime configuration from `application.yml` and
the environment-specific property files.

The following property names are used by the current application
configuration; provide the appropriate values for your environment:

  Variable        Purpose
  --------------- -----------------------------------------------
  `SERVER_PORT`   HTTP port used by the Spring Boot application
  `DB_HOST`       PostgreSQL host
  `DB_PORT`       PostgreSQL port
  `DB_NAME`       Database name
  `DB_USERNAME`   Database username
  `DB_PASSWORD`   Database password

The application configuration may define defaults for local development.
Use explicit, environment-specific values for shared environments.

### RSA keys

If JWT signing is configured with an RSA key pair, provide the private
and public key files at the paths configured by the application.

-   Keep the private key confidential.
-   Do not add private keys to Git.
-   Restrict filesystem permissions so only the application account can
    read the private key.
-   Use separate keys for development and production environments.

## Database migrations

Flyway is used to manage database schema changes.

Migration scripts are conventionally stored under:

``` text
src/main/resources/db/migration/
```

Use versioned migration names, for example:

``` text
V1__create_initial_tables.sql
V2__add_product_reference.sql
```

Important:

-   Do not edit a migration that has already been applied in a shared
    environment.
-   Add a new versioned migration for each database change.
-   Keep `spring.jpa.hibernate.ddl-auto` set to `none` when Flyway is
    responsible for schema evolution.
-   A schema-per-tenant design requires migrations to be applied to
    every tenant schema that needs the change. Verify the branch's
    tenant migration orchestration before creating or onboarding
    tenants.

## Running the application

After configuring the database and environment:

``` bash
mvn spring-boot:run
```

Or run the packaged JAR:

``` bash
java -jar target/inventory-saas-0.0.1-SNAPSHOT.jar
```

Adjust the JAR filename if the Maven version or artifact name changes.

The application port is controlled by `SERVER_PORT` (with the default
configured in `application.yml`).

## API documentation

When the application is running and Springdoc is enabled, the OpenAPI
specification and Swagger UI are available at:

-   OpenAPI JSON: `http://localhost:<SERVER_PORT>/v3/api-docs`
-   Swagger UI: `http://localhost:<SERVER_PORT>/swagger-ui/index.html`

Replace `<SERVER_PORT>` with the port configured for your environment.

If the endpoints are unavailable, check the active Spring profile,
Springdoc configuration, application startup logs, and Spring Security
access rules.

## Testing

Run the backend verification lifecycle:

``` bash
mvn clean verify
```

Run tests only:

``` bash
mvn test
```

Add tests for tenant isolation whenever implementing tenant-scoped
functionality. In particular, verify that a user authenticated in one
organization cannot read or modify another organization's data.

## Deployment

For a Linux VM deployment, the application can be run as a `systemd`
service under a dedicated, non-login operating-system account.

A typical deployment layout is:

``` text
/opt/inventory-saas/
├── inventory-saas.jar
├── .env.spring
├── secrets/
└── logs/
    └── application.log
```

Recommended deployment practices:

1.  Build and test the JAR in CI.
2.  Transfer the artifact to the VM.
3.  Install it using a stable filename such as `inventory-saas.jar`.
4.  Keep environment configuration and RSA keys outside the Git
    repository.
5.  Restart the `inventory-saas` systemd service.
6.  Check service status and application logs.

Useful commands:

``` bash
sudo systemctl status inventory-saas
sudo systemctl restart inventory-saas
sudo journalctl -u inventory-saas -f
sudo tail -f /opt/inventory-saas/logs/application.log
```

The actual log file path depends on the `logging.file.name` property
configured in `application.yml`.

## Security notes

-   Never commit `.env.spring`, production credentials, JWT secrets, or
    private RSA keys.
-   Do not log passwords, access tokens, private keys, or other
    sensitive values.
-   Derive tenant identity from the trusted authentication context, not
    from an untrusted request parameter.
-   Enforce tenant isolation at the persistence/data-access boundary.
-   Test cross-tenant access explicitly.
-   Use least-privilege database and Linux accounts in deployed
    environments.

## Project status and contribution

This README documents the known project setup and the schema-per-tenant
direction of the feature branch. Some implementation details---such as
the exact tenant resolver, schema naming strategy and
tenant migration orchestration---must be confirmed against the source
code before treating this document as a complete implementation
reference.

When contributing:

1.  Create a focused feature branch.
2.  Keep database changes in new Flyway migrations.
3.  Add or update automated tests.
4.  Run `mvn clean verify` before opening a pull request.
5.  Never include local environment files or secrets in commits.
