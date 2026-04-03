package pro.saransh.springboot.rbac.filter;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import pro.saransh.springboot.rbac.context.RbacContext;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.core.Role;
import pro.saransh.springboot.rbac.core.SimpleRbacUserDetails;
import pro.saransh.springboot.rbac.filter.RbacContextFilter;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class RbacContextFilterTest {

    private final RbacUserDetails testUser = new SimpleRbacUserDetails("user1",
            Set.of(new Role("ADMIN", Set.of(Permission.of("users:read")))),
            Set.of());

    @Test
    void shouldBindScopedValueWhenPrincipalFound() throws Exception {
        RbacContextFilter filter = new RbacContextFilter(
                request -> "user1",
                principal -> testUser
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicReference<RbacUserDetails> captured = new AtomicReference<>();

        FilterChain chain = (req, res) -> captured.set(RbacContext.currentUser());

        filter.doFilter(request, response, chain);

        assertNotNull(captured.get());
        assertEquals("user1", captured.get().principal());
    }

    @Test
    void shouldProceedUnboundWhenPrincipalIsNull() throws Exception {
        RbacContextFilter filter = new RbacContextFilter(
                request -> null,
                principal -> testUser
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean chainCalled = new AtomicBoolean(false);
        AtomicBoolean wasAuthenticated = new AtomicBoolean(true);

        FilterChain chain = (req, res) -> {
            chainCalled.set(true);
            wasAuthenticated.set(RbacContext.isAuthenticated());
        };

        filter.doFilter(request, response, chain);

        assertTrue(chainCalled.get());
        assertFalse(wasAuthenticated.get());
    }

    @Test
    void shouldProceedUnboundWhenUserNotFound() throws Exception {
        RbacContextFilter filter = new RbacContextFilter(
                request -> "unknown",
                principal -> null
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        AtomicBoolean wasAuthenticated = new AtomicBoolean(true);

        FilterChain chain = (req, res) -> wasAuthenticated.set(RbacContext.isAuthenticated());

        filter.doFilter(request, response, chain);

        assertFalse(wasAuthenticated.get());
    }

    @Test
    void shouldNotBeBoundAfterFilterCompletes() throws Exception {
        RbacContextFilter filter = new RbacContextFilter(
                request -> "user1",
                principal -> testUser
        );

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = (req, res) -> assertTrue(RbacContext.isAuthenticated());

        filter.doFilter(request, response, chain);

        assertFalse(RbacContext.isAuthenticated());
    }
}
