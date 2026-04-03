package pro.saransh.springboot.policy.context;

import org.junit.jupiter.api.Test;
import pro.saransh.springboot.rbac.context.RbacContext;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.core.SimpleRbacUserDetails;
import pro.saransh.springboot.rbac.core.Role;
import pro.saransh.springboot.rbac.exception.AccessDeniedException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RbacContextTest {

    private final RbacUserDetails testUser = new SimpleRbacUserDetails("user1",
            Set.of(new Role("ADMIN", Set.of(Permission.of("users:read")))),
            Set.of());

    @Test
    void shouldThrowWhenNotBound() {
        assertThrows(AccessDeniedException.class, RbacContext::currentUser);
    }

    @Test
    void shouldReturnFalseWhenNotAuthenticated() {
        assertFalse(RbacContext.isAuthenticated());
    }

    @Test
    void shouldReturnUserWhenBound() throws Exception {
        ScopedValue.where(RbacContext.scopedValue(), testUser).call(() -> {
            assertEquals(testUser, RbacContext.currentUser());
            return null;
        });
    }

    @Test
    void shouldReturnTrueWhenAuthenticated() throws Exception {
        ScopedValue.where(RbacContext.scopedValue(), testUser).call(() -> {
            assertTrue(RbacContext.isAuthenticated());
            return null;
        });
    }

    @Test
    void shouldNotBeBoundAfterScopeEnds() throws Exception {
        ScopedValue.where(RbacContext.scopedValue(), testUser).call(() -> {
            assertTrue(RbacContext.isAuthenticated());
            return null;
        });

        assertFalse(RbacContext.isAuthenticated());
    }
}
