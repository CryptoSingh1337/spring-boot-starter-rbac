package pro.saransh.examples.rbac.basic;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.core.Role;
import pro.saransh.springboot.rbac.core.SimpleRbacUserDetails;
import pro.saransh.springboot.rbac.spi.PrincipalExtractor;
import pro.saransh.springboot.rbac.spi.RbacUserDetailsService;

import java.util.Map;
import java.util.Set;

@Configuration
public class ExampleRbacConfiguration {

    private static final Map<String, RbacUserDetails> USERS = Map.of(
            "admin", new SimpleRbacUserDetails("admin",
                    Set.of(new Role("ADMIN", Set.of(
                            Permission.of("users:read"),
                            Permission.of("users:write")))),
                    Set.of(Permission.of("audit:read"))),
            "viewer", new SimpleRbacUserDetails("viewer",
                    Set.of(new Role("VIEWER", Set.of(
                            Permission.of("users:read")))),
                    Set.of())
    );

    @Bean
    public PrincipalExtractor principalExtractor() {
        return request -> request.getHeader("X-User-Id");
    }

    @Bean
    public RbacUserDetailsService rbacUserDetailsService() {
        return USERS::get;
    }
}
