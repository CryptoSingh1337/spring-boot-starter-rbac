package pro.saransh.springboot.rbac.spi;

import pro.saransh.springboot.rbac.core.RbacUserDetails;

@FunctionalInterface
public interface RbacUserDetailsService {
    RbacUserDetails loadUserByPrincipal(String principal);
}
