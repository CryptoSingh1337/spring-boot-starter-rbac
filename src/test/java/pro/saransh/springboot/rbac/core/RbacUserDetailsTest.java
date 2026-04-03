package pro.saransh.springboot.rbac.core;

import org.junit.jupiter.api.Test;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.core.Role;
import pro.saransh.springboot.rbac.core.SimpleRbacUserDetails;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RbacUserDetailsTest {

    private final Role adminRole = new Role("ADMIN",
            Set.of(Permission.of("users:read"), Permission.of("users:write")));
    private final Role viewerRole = new Role("VIEWER",
            Set.of(Permission.of("users:read"), Permission.of("reports:read")));

    @Test
    void shouldMergeEffectivePermissions() {
        RbacUserDetails user = new SimpleRbacUserDetails("user1",
                Set.of(adminRole, viewerRole),
                Set.of(Permission.of("audit:read")));

        Set<Permission> effective = user.effectivePermissions();

        assertTrue(effective.contains(Permission.of("users:read")));
        assertTrue(effective.contains(Permission.of("users:write")));
        assertTrue(effective.contains(Permission.of("reports:read")));
        assertTrue(effective.contains(Permission.of("audit:read")));
        assertEquals(4, effective.size());
    }

    @Test
    void shouldCheckHasRole() {
        RbacUserDetails user = new SimpleRbacUserDetails("user1", Set.of(adminRole), Set.of());

        assertTrue(user.hasRole("ADMIN"));
        assertFalse(user.hasRole("VIEWER"));
    }

    @Test
    void shouldCheckHasPermissionFromRole() {
        RbacUserDetails user = new SimpleRbacUserDetails("user1", Set.of(adminRole), Set.of());

        assertTrue(user.hasPermission("users:read"));
        assertFalse(user.hasPermission("audit:read"));
    }

    @Test
    void shouldCheckHasDirectPermission() {
        RbacUserDetails user = new SimpleRbacUserDetails("user1",
                Set.of(), Set.of(Permission.of("audit:read")));

        assertTrue(user.hasPermission("audit:read"));
        assertFalse(user.hasPermission("users:read"));
    }

    @Test
    void shouldReturnRoleNames() {
        RbacUserDetails user = new SimpleRbacUserDetails("user1",
                Set.of(adminRole, viewerRole), Set.of());

        Set<String> names = user.roleNames();
        assertEquals(Set.of("ADMIN", "VIEWER"), names);
    }

    @Test
    void shouldHandleNullRolesAndPermissions() {
        RbacUserDetails user = new SimpleRbacUserDetails("user1", null, null);

        assertTrue(user.roles().isEmpty());
        assertTrue(user.directPermissions().isEmpty());
        assertTrue(user.effectivePermissions().isEmpty());
    }

    @Test
    void shouldRejectNullPrincipal() {
        assertThrows(NullPointerException.class,
                () -> new SimpleRbacUserDetails(null, Set.of(), Set.of()));
    }

    @Test
    void shouldRejectBlankPrincipal() {
        assertThrows(IllegalArgumentException.class,
                () -> new SimpleRbacUserDetails("  ", Set.of(), Set.of()));
    }
}
