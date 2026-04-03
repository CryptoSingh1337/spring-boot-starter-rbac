package pro.saransh.springboot.rbac.context;

import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.exception.AccessDeniedException;

public final class RbacContext {

    private static final ScopedValue<RbacUserDetails> CURRENT_USER = ScopedValue.newInstance();

    private RbacContext() {
    }

    public static ScopedValue<RbacUserDetails> scopedValue() {
        return CURRENT_USER;
    }

    public static RbacUserDetails currentUser() {
        if (!CURRENT_USER.isBound()) {
            throw new AccessDeniedException(
                    "No authenticated user in current scope. "
                            + "Ensure RbacContextFilter is active and a valid principal was extracted.");
        }
        return CURRENT_USER.get();
    }

    public static boolean isAuthenticated() {
        return CURRENT_USER.isBound();
    }
}
