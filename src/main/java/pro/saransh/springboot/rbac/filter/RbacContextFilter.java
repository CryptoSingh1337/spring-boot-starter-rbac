package pro.saransh.springboot.rbac.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import pro.saransh.springboot.rbac.context.RbacContext;
import pro.saransh.springboot.rbac.core.RbacUserDetails;
import pro.saransh.springboot.rbac.spi.PrincipalExtractor;
import pro.saransh.springboot.rbac.spi.RbacUserDetailsService;

import java.io.IOException;

public class RbacContextFilter implements Filter {

    private final PrincipalExtractor principalExtractor;
    private final RbacUserDetailsService rbacUserDetailsService;

    public RbacContextFilter(PrincipalExtractor principalExtractor,
                               RbacUserDetailsService rbacUserDetailsService) {
        this.principalExtractor = principalExtractor;
        this.rbacUserDetailsService = rbacUserDetailsService;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String principal = principalExtractor.extract(request);

        if (principal == null) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        RbacUserDetails userDetails = rbacUserDetailsService.loadUserByPrincipal(principal);
        if (userDetails == null) {
            chain.doFilter(servletRequest, servletResponse);
            return;
        }

        try {
            ScopedValue.where(RbacContext.scopedValue(), userDetails)
                    .call(() -> {
                        chain.doFilter(servletRequest, servletResponse);
                        return null;
                    });
        } catch (IOException | ServletException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
