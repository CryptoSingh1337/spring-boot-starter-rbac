package pro.saransh.springboot.rbac.core;

import org.junit.jupiter.api.Test;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.Role;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleTest {

    @Test
    void shouldCreateRoleWithPermissions() {
        Set<Permission> permissions = Set.of(Permission.of("users:read"), Permission.of("users:write"));
        Role role = new Role("ADMIN", permissions);

        assertEquals("ADMIN", role.name());
        assertEquals(2, role.permissions().size());
    }

    @Test
    void shouldDefensiveCopyPermissions() {
        Set<Permission> permissions = new HashSet<>();
        permissions.add(Permission.of("users:read"));

        Role role = new Role("ADMIN", permissions);
        permissions.add(Permission.of("users:write"));

        assertEquals(1, role.permissions().size());
    }

    @Test
    void shouldReturnImmutablePermissions() {
        Role role = new Role("ADMIN", Set.of(Permission.of("users:read")));
        assertThrows(UnsupportedOperationException.class,
                () -> role.permissions().add(Permission.of("users:write")));
    }

    @Test
    void shouldHandleNullPermissions() {
        Role role = new Role("VIEWER", null);
        assertTrue(role.permissions().isEmpty());
    }

    @Test
    void shouldCheckHasPermission() {
        Role role = new Role("ADMIN", Set.of(Permission.of("users:read"), Permission.of("users:write")));

        assertTrue(role.hasPermission("users:read"));
        assertFalse(role.hasPermission("users:delete"));
    }

    @Test
    void shouldRejectNullName() {
        assertThrows(NullPointerException.class, () -> new Role(null, Set.of()));
    }

    @Test
    void shouldRejectBlankName() {
        assertThrows(IllegalArgumentException.class, () -> new Role("  ", Set.of()));
    }
}
