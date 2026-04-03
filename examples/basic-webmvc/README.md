# Basic WebMVC Example

This example shows how to use `spring-boot-starter-rbac` in a Spring Boot MVC application.

## Prerequisites

- Java 25
- Maven

## Run

Install the starter into your local Maven repository from the repository root:

```bash
mvn install
```

Start the example app:

```bash
cd examples/basic-webmvc
mvn spring-boot:run
```

## How it works

- `PrincipalExtractor` reads the user identity from the `X-User-Id` header
- `RbacUserDetailsService` returns in-memory RBAC data for that principal
- Controller methods are protected with the starter annotations

Available users:

- `admin`
- `viewer`

## Try it

Public endpoint:

```bash
curl localhost:8080/public
```

Admin-only endpoint:

```bash
curl -H 'X-User-Id: admin' localhost:8080/admin-only
curl -H 'X-User-Id: viewer' localhost:8080/admin-only
```

Role-based access:

```bash
curl -H 'X-User-Id: viewer' localhost:8080/any-role
```

Permission-based access:

```bash
curl -H 'X-User-Id: admin' localhost:8080/all-permissions
curl -H 'X-User-Id: viewer' localhost:8080/all-permissions
curl -H 'X-User-Id: admin' localhost:8080/any-permission
```

Current user details:

```bash
curl -H 'X-User-Id: admin' localhost:8080/me
curl -H 'X-User-Id: viewer' localhost:8080/me
```
