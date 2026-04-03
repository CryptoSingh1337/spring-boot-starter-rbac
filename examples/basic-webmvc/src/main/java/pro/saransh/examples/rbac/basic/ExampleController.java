package pro.saransh.examples.rbac.basic;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pro.saransh.springboot.rbac.annotation.HasAnyPermission;
import pro.saransh.springboot.rbac.annotation.HasAnyRole;
import pro.saransh.springboot.rbac.annotation.HasPermissions;
import pro.saransh.springboot.rbac.annotation.HasRole;
import pro.saransh.springboot.rbac.context.RbacContext;

import java.util.Map;

@RestController
public class ExampleController {

    @GetMapping("/public")
    public Map<String, String> publicEndpoint() {
        return Map.of("message", "public");
    }

    @HasRole("ADMIN")
    @GetMapping("/admin-only")
    public Map<String, String> adminOnly() {
        return Map.of("message", "admin");
    }

    @HasAnyRole({"ADMIN", "VIEWER"})
    @GetMapping("/any-role")
    public Map<String, String> anyRole() {
        return Map.of("message", "any-role");
    }

    @HasPermissions({"users:read", "users:write"})
    @GetMapping("/all-permissions")
    public Map<String, String> allPermissions() {
        return Map.of("message", "all-permissions");
    }

    @HasAnyPermission({"audit:read", "audit:write"})
    @GetMapping("/any-permission")
    public Map<String, String> anyPermission() {
        return Map.of("message", "any-permission");
    }

    @HasAnyRole({"ADMIN", "VIEWER"})
    @GetMapping("/me")
    public Map<String, Object> me() {
        var user = RbacContext.currentUser();
        return Map.of(
                "principal", user.principal(),
                "roles", user.roleNames(),
                "permissions", user.effectivePermissions().stream().map(Object::toString).sorted().toList()
        );
    }
}
