package pro.saransh.springboot.rbac.core;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public interface RbacUserDetails {

    String principal();

    Set<Role> roles();

    Set<Permission> directPermissions();

    default Set<Permission> effectivePermissions() {
        Set<Permission> effective = new HashSet<>(directPermissions());
        for (Role role : roles()) {
            effective.addAll(role.permissions());
        }
        return Set.copyOf(effective);
    }

    default boolean hasRole(String roleName) {
        return roles().stream().anyMatch(r -> r.name().equals(roleName));
    }

    default boolean hasPermission(String permissionValue) {
        if (directPermissions().stream().anyMatch(p -> p.value().equals(permissionValue))) {
            return true;
        }
        return roles().stream().anyMatch(r -> r.hasPermission(permissionValue));
    }

    default Set<String> roleNames() {
        return roles().stream()
                .map(Role::name)
                .collect(Collectors.toUnmodifiableSet());
    }
}
