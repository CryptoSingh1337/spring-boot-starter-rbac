package pro.saransh.springboot.rbac.spi;

import jakarta.servlet.http.HttpServletRequest;

@FunctionalInterface
public interface PrincipalExtractor {
    String extract(HttpServletRequest request);
}
