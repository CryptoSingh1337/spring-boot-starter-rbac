package pro.saransh.springboot.policy.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.saransh.springboot.rbac.annotation.HasAnyPermission;
import pro.saransh.springboot.rbac.annotation.HasAnyRole;
import pro.saransh.springboot.rbac.annotation.HasPermissions;
import pro.saransh.springboot.rbac.annotation.HasRole;
import pro.saransh.springboot.rbac.config.RbacAutoConfiguration;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.core.Role;
import pro.saransh.springboot.rbac.core.SimpleRbacUserDetails;
import pro.saransh.springboot.rbac.exception.AccessDeniedException;
import pro.saransh.springboot.rbac.spi.PrincipalExtractor;
import pro.saransh.springboot.rbac.spi.RbacUserDetailsService;

import java.util.Map;
import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import({RbacIntegrationTest.TestConfig.class, RbacIntegrationTest.TestController.class})
@ImportAutoConfiguration(RbacAutoConfiguration.class)
class RbacIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Configuration
    static class TestConfig {

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
        PrincipalExtractor principalExtractor() {
            return request -> request.getHeader("X-User-Id");
        }

        @Bean
        RbacUserDetailsService rbacUserDetailsService() {
            return principal -> USERS.get(principal);
        }
    }

    @RestController
    static class TestController {

        @GetMapping("/public")
        String publicEndpoint() {
            return "public";
        }

        @HasRole("ADMIN")
        @GetMapping("/admin-only")
        String adminOnly() {
            return "admin";
        }

        @HasAnyRole({"ADMIN", "VIEWER"})
        @GetMapping("/any-role")
        String anyRole() {
            return "any-role";
        }

        @HasPermissions({"users:read", "users:write"})
        @GetMapping("/all-permissions")
        String allPermissions() {
            return "all-permissions";
        }

        @HasAnyPermission({"audit:read", "audit:write"})
        @GetMapping("/any-permission")
        String anyPermission() {
            return "any-permission";
        }

        @org.springframework.web.bind.annotation.ExceptionHandler(AccessDeniedException.class)
        ResponseEntity<String> handleAccessDenied(AccessDeniedException ex) {
            return ResponseEntity.status(403).body(ex.getMessage());
        }
    }

    @Test
    void publicEndpoint_shouldAllowAnonymous() throws Exception {
        mockMvc.perform(get("/public"))
                .andExpect(status().isOk())
                .andExpect(content().string("public"));
    }

    @Test
    void hasRole_shouldAllowAdmin() throws Exception {
        mockMvc.perform(get("/admin-only").header("X-User-Id", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("admin"));
    }

    @Test
    void hasRole_shouldDenyViewer() throws Exception {
        mockMvc.perform(get("/admin-only").header("X-User-Id", "viewer"))
                .andExpect(status().isForbidden());
    }

    @Test
    void hasRole_shouldDenyAnonymous() throws Exception {
        mockMvc.perform(get("/admin-only"))
                .andExpect(status().isForbidden());
    }

    @Test
    void hasAnyRole_shouldAllowViewer() throws Exception {
        mockMvc.perform(get("/any-role").header("X-User-Id", "viewer"))
                .andExpect(status().isOk())
                .andExpect(content().string("any-role"));
    }

    @Test
    void hasPermissions_shouldAllowAdminWithAllPermissions() throws Exception {
        mockMvc.perform(get("/all-permissions").header("X-User-Id", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("all-permissions"));
    }

    @Test
    void hasPermissions_shouldDenyViewerMissingWritePermission() throws Exception {
        mockMvc.perform(get("/all-permissions").header("X-User-Id", "viewer"))
                .andExpect(status().isForbidden());
    }

    @Test
    void hasAnyPermission_shouldAllowAdminWithDirectPermission() throws Exception {
        mockMvc.perform(get("/any-permission").header("X-User-Id", "admin"))
                .andExpect(status().isOk())
                .andExpect(content().string("any-permission"));
    }

    @Test
    void hasAnyPermission_shouldDenyViewerWithoutPermission() throws Exception {
        mockMvc.perform(get("/any-permission").header("X-User-Id", "viewer"))
                .andExpect(status().isForbidden());
    }
}
