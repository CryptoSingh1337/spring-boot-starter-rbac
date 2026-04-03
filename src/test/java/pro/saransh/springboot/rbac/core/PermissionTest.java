package pro.saransh.springboot.rbac.core;

import org.junit.jupiter.api.Test;
import pro.saransh.springboot.rbac.core.Permission;

import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldCreatePermission() {
        Permission permission = Permission.of("users:read");
        assertEquals("users:read", permission.value());
    }

    @Test
    void shouldReturnValueAsToString() {
        assertEquals("users:write", Permission.of("users:write").toString());
    }

    @Test
    void shouldRejectNullValue() {
        assertThrows(NullPointerException.class, () -> new Permission(null));
    }

    @Test
    void shouldRejectBlankValue() {
        assertThrows(IllegalArgumentException.class, () -> Permission.of("  "));
    }

    @Test
    void shouldBeEqualForSameValue() {
        assertEquals(Permission.of("users:read"), Permission.of("users:read"));
    }

    @Test
    void shouldNotBeEqualForDifferentValues() {
        assertNotEquals(Permission.of("users:read"), Permission.of("users:write"));
    }
}
