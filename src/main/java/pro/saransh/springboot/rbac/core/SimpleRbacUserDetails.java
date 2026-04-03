package pro.saransh.springboot.rbac.core;

import java.util.Objects;
import java.util.Set;

public record SimpleRbacUserDetails(String principal, Set<Role> roles, Set<Permission> directPermissions)
        implements RbacUserDetails {

    public SimpleRbacUserDetails {
        Objects.requireNonNull(principal, "Principal must not be null");
        if (principal.isBlank()) {
            throw new IllegalArgumentException("Principal must not be blank");
        }
        roles = roles == null ? Set.of() : Set.copyOf(roles);
        directPermissions = directPermissions == null ? Set.of() : Set.copyOf(directPermissions);
    }
}
