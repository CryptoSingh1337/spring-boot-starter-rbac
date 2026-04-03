package pro.saransh.springboot.policy.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Test;
import pro.saransh.springboot.rbac.annotation.HasAnyPermission;
import pro.saransh.springboot.rbac.annotation.HasAnyRole;
import pro.saransh.springboot.rbac.annotation.HasPermissions;
import pro.saransh.springboot.rbac.annotation.HasRole;
import pro.saransh.springboot.rbac.aspect.RbacEnforcementAspect;
import pro.saransh.springboot.rbac.context.RbacContext;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.core.SimpleRbacUserDetails;
import pro.saransh.springboot.rbac.core.Role;
import pro.saransh.springboot.rbac.exception.AccessDeniedException;

import java.lang.annotation.Annotation;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RbacEnforcementAspectTest {

    private final RbacEnforcementAspect aspect = new RbacEnforcementAspect();

    private final RbacUserDetails adminUser = new SimpleRbacUserDetails("admin",
            Set.of(new Role("ADMIN", Set.of(Permission.of("users:read"), Permission.of("users:write")))),
            Set.of(Permission.of("audit:read")));

    private ProceedingJoinPoint mockJoinPoint() throws Throwable {
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);
        when(jp.proceed()).thenReturn("success");
        return jp;
    }

    // --- @HasRole tests ---

    @Test
    void hasRole_shouldAllowWhenUserHasRole() throws Throwable {
        ProceedingJoinPoint jp = mockJoinPoint();
        HasRole annotation = createHasRole("ADMIN");

        Object result = ScopedValue.where(RbacContext.scopedValue(), adminUser)
                .call(() -> aspect.enforceHasRole(jp, annotation));

        assertEquals("success", result);
        verify(jp).proceed();
    }

    @Test
    void hasRole_shouldDenyWhenUserLacksRole() {
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);
        HasRole annotation = createHasRole("SUPER_ADMIN");

        assertThrows(AccessDeniedException.class, () ->
                ScopedValue.where(RbacContext.scopedValue(), adminUser)
                        .call(() -> aspect.enforceHasRole(jp, annotation)));
    }

    // --- @HasAnyRole tests ---

    @Test
    void hasAnyRole_shouldAllowWhenUserHasOneOfRoles() throws Throwable {
        ProceedingJoinPoint jp = mockJoinPoint();
        HasAnyRole annotation = createHasAnyRole("ADMIN", "MANAGER");

        Object result = ScopedValue.where(RbacContext.scopedValue(), adminUser)
                .call(() -> aspect.enforceHasAnyRole(jp, annotation));

        assertEquals("success", result);
    }

    @Test
    void hasAnyRole_shouldDenyWhenUserHasNoneOfRoles() {
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);
        HasAnyRole annotation = createHasAnyRole("MANAGER", "SUPER_ADMIN");

        assertThrows(AccessDeniedException.class, () ->
                ScopedValue.where(RbacContext.scopedValue(), adminUser)
                        .call(() -> aspect.enforceHasAnyRole(jp, annotation)));
    }

    // --- @HasPermissions tests ---

    @Test
    void hasPermissions_shouldAllowWhenUserHasAllPermissions() throws Throwable {
        ProceedingJoinPoint jp = mockJoinPoint();
        HasPermissions annotation = createHasPermissions("users:read", "users:write");

        Object result = ScopedValue.where(RbacContext.scopedValue(), adminUser)
                .call(() -> aspect.enforceHasPermissions(jp, annotation));

        assertEquals("success", result);
    }

    @Test
    void hasPermissions_shouldDenyWhenUserLacksOnePermission() {
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);
        HasPermissions annotation = createHasPermissions("users:read", "users:delete");

        assertThrows(AccessDeniedException.class, () ->
                ScopedValue.where(RbacContext.scopedValue(), adminUser)
                        .call(() -> aspect.enforceHasPermissions(jp, annotation)));
    }

    // --- @HasAnyPermission tests ---

    @Test
    void hasAnyPermission_shouldAllowWhenUserHasOnePermission() throws Throwable {
        ProceedingJoinPoint jp = mockJoinPoint();
        HasAnyPermission annotation = createHasAnyPermission("users:delete", "audit:read");

        Object result = ScopedValue.where(RbacContext.scopedValue(), adminUser)
                .call(() -> aspect.enforceHasAnyPermission(jp, annotation));

        assertEquals("success", result);
    }

    @Test
    void hasAnyPermission_shouldDenyWhenUserHasNoneOfPermissions() {
        ProceedingJoinPoint jp = mock(ProceedingJoinPoint.class);
        HasAnyPermission annotation = createHasAnyPermission("users:delete", "reports:write");

        assertThrows(AccessDeniedException.class, () ->
                ScopedValue.where(RbacContext.scopedValue(), adminUser)
                        .call(() -> aspect.enforceHasAnyPermission(jp, annotation)));
    }

    // --- Annotation factory helpers ---

    private HasRole createHasRole(String value) {
        return new HasRole() {
            @Override public String value() { return value; }
            @Override public Class<? extends Annotation> annotationType() { return HasRole.class; }
        };
    }

    private HasAnyRole createHasAnyRole(String... values) {
        return new HasAnyRole() {
            @Override public String[] value() { return values; }
            @Override public Class<? extends Annotation> annotationType() { return HasAnyRole.class; }
        };
    }

    private HasPermissions createHasPermissions(String... values) {
        return new HasPermissions() {
            @Override public String[] value() { return values; }
            @Override public Class<? extends Annotation> annotationType() { return HasPermissions.class; }
        };
    }

    private HasAnyPermission createHasAnyPermission(String... values) {
        return new HasAnyPermission() {
            @Override public String[] value() { return values; }
            @Override public Class<? extends Annotation> annotationType() { return HasAnyPermission.class; }
        };
    }
}
