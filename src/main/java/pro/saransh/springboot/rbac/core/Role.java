package pro.saransh.springboot.rbac.core;

import java.util.Objects;
import java.util.Set;

public record Role(String name, Set<Permission> permissions) {

    public Role {
        Objects.requireNonNull(name, "Role name must not be null");
        if (name.isBlank()) {
            throw new IllegalArgumentException("Role name must not be blank");
        }
        permissions = permissions == null ? Set.of() : Set.copyOf(permissions);
    }

    public boolean hasPermission(String permissionValue) {
        return permissions.stream()
                .anyMatch(p -> p.value().equals(permissionValue));
    }
}
