# spring-boot-starter-rbac

Spring Boot starter for annotation-driven RBAC enforcement.

## What it provides

- RBAC method annotations:
  - `@HasRole`
  - `@HasAnyRole`
  - `@HasPermissions`
  - `@HasAnyPermission`
- Auto-configuration for:
  - the RBAC enforcement aspect
  - request-scoped RBAC user resolution for Servlet applications
- SPI hooks for integrating with your application's authentication model:
  - `PrincipalExtractor`
  - `RbacUserDetailsService`

## Installation

Add the starter dependency to your Spring Boot application:

```xml
<dependency>
    <groupId>pro.saransh</groupId>
    <artifactId>spring-boot-starter-rbac</artifactId>
    <version>0.0.1</version>
</dependency>
```

## Minimal setup

Your application must provide two beans:

1. `PrincipalExtractor`
2. `RbacUserDetailsService`

Example configuration:

```java
@Configuration
public class RbacConfig {

    @Bean
    PrincipalExtractor principalExtractor() {
        return request -> request.getHeader("X-User-Id");
    }

    @Bean
    RbacUserDetailsService rbacUserDetailsService() {
        return principal -> new SimpleRbacUserDetails(
                principal,
                Set.of(new Role("ADMIN", Set.of(Permission.of("users:read")))),
                Set.of()
        );
    }
}
```

## Usage

Protect controller or service methods with the provided annotations:

```java
@RestController
class UserController {

    @HasRole("ADMIN")
    @GetMapping("/admin-only")
    String adminOnly() {
        return "admin";
    }

    @HasPermissions({"users:read", "users:write"})
    @GetMapping("/users")
    String users() {
        return "users";
    }
}
```

## Configuration

Supported properties:

```yaml
rbac:
  enabled: true
  filter-order: 0
```

## Example project

A runnable example application is available at:

- `examples/basic-webmvc`

Run it locally:

```bash
mvn install
cd examples/basic-webmvc
mvn spring-boot:run
```

The example uses `X-User-Id` as the incoming principal and includes public and protected endpoints.

## Development

Build and test the starter:

```bash
mvn package
```
