package pro.saransh.springboot.rbac.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import pro.saransh.springboot.rbac.annotation.HasAnyPermission;
import pro.saransh.springboot.rbac.annotation.HasAnyRole;
import pro.saransh.springboot.rbac.annotation.HasPermissions;
import pro.saransh.springboot.rbac.annotation.HasRole;
import pro.saransh.springboot.rbac.context.RbacContext;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.exception.AccessDeniedException;

import java.util.Arrays;

@Aspect
public class RbacEnforcementAspect {

    @Around("@annotation(hasRole)")
    public Object enforceHasRole(ProceedingJoinPoint joinPoint, HasRole hasRole) throws Throwable {
        RbacUserDetails user = RbacContext.currentUser();

        if (!user.hasRole(hasRole.value())) {
            throw new AccessDeniedException(
                    "User '%s' does not have required role: %s".formatted(user.principal(), hasRole.value()));
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(hasAnyRole)")
    public Object enforceHasAnyRole(ProceedingJoinPoint joinPoint, HasAnyRole hasAnyRole) throws Throwable {
        RbacUserDetails user = RbacContext.currentUser();
        String[] required = hasAnyRole.value();

        boolean authorized = Arrays.stream(required).anyMatch(user::hasRole);

        if (!authorized) {
            throw new AccessDeniedException(
                    "User '%s' does not have any of the required roles: %s"
                            .formatted(user.principal(), Arrays.toString(required)));
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(hasPermissions)")
    public Object enforceHasPermissions(ProceedingJoinPoint joinPoint, HasPermissions hasPermissions) throws Throwable {
        RbacUserDetails user = RbacContext.currentUser();
        String[] required = hasPermissions.value();

        boolean authorized = Arrays.stream(required).allMatch(user::hasPermission);

        if (!authorized) {
            throw new AccessDeniedException(
                    "User '%s' does not have all required permissions: %s"
                            .formatted(user.principal(), Arrays.toString(required)));
        }
        return joinPoint.proceed();
    }

    @Around("@annotation(hasAnyPermission)")
    public Object enforceHasAnyPermission(ProceedingJoinPoint joinPoint, HasAnyPermission hasAnyPermission) throws Throwable {
        RbacUserDetails user = RbacContext.currentUser();
        String[] required = hasAnyPermission.value();

        boolean authorized = Arrays.stream(required).anyMatch(user::hasPermission);

        if (!authorized) {
            throw new AccessDeniedException(
                    "User '%s' does not have any of the required permissions: %s"
                            .formatted(user.principal(), Arrays.toString(required)));
        }
        return joinPoint.proceed();
    }
}
