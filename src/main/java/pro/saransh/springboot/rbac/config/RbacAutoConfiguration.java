package pro.saransh.springboot.rbac.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import pro.saransh.springboot.rbac.aspect.RbacEnforcementAspect;
import pro.saransh.springboot.rbac.filter.RbacContextFilter;
import pro.saransh.springboot.rbac.spi.PrincipalExtractor;
import pro.saransh.springboot.rbac.spi.RbacUserDetailsService;

@AutoConfiguration
@EnableAspectJAutoProxy
@EnableConfigurationProperties(RbacProperties.class)
@ConditionalOnProperty(prefix = "rbac", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.SERVLET)
public class RbacAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RbacEnforcementAspect rbacEnforcementAspect() {
        return new RbacEnforcementAspect();
    }

    @Bean
    @ConditionalOnBean({PrincipalExtractor.class, RbacUserDetailsService.class})
    @ConditionalOnMissingBean
    public RbacContextFilter rbacContextFilter(PrincipalExtractor principalExtractor,
                                                   RbacUserDetailsService rbacUserDetailsService) {
        return new RbacContextFilter(principalExtractor, rbacUserDetailsService);
    }

    @Bean
    @ConditionalOnBean(RbacContextFilter.class)
    public FilterRegistrationBean<RbacContextFilter> rbacContextFilterRegistration(
            RbacContextFilter filter, RbacProperties properties) {
        FilterRegistrationBean<RbacContextFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setOrder(properties.getFilterOrder());
        registration.setName("rbacContextFilter");
        return registration;
    }
}
