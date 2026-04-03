package pro.saransh.springboot.policy.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import pro.saransh.springboot.rbac.aspect.RbacEnforcementAspect;
import pro.saransh.springboot.rbac.config.RbacAutoConfiguration;
import pro.saransh.springboot.rbac.core.Permission;
import pro.saransh.springboot.rbac.core.Role;
import pro.saransh.springboot.rbac.core.SimpleRbacUserDetails;
import pro.saransh.springboot.rbac.filter.RbacContextFilter;
import pro.saransh.springboot.rbac.spi.PrincipalExtractor;
import pro.saransh.springboot.rbac.spi.RbacUserDetailsService;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class RbacAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RbacAutoConfiguration.class));

    @Test
    void shouldRegisterAspectByDefault() {
        contextRunner.run(context ->
                assertThat(context).hasSingleBean(RbacEnforcementAspect.class));
    }

    @Test
    void shouldNotRegisterFilterWithoutSPIBeans() {
        contextRunner.run(context ->
                assertThat(context).doesNotHaveBean(RbacContextFilter.class));
    }

    @Test
    void shouldRegisterFilterWhenSPIBeansPresent() {
        contextRunner
                .withBean(PrincipalExtractor.class, () -> request -> "user1")
                .withBean(RbacUserDetailsService.class, () -> principal ->
                        new SimpleRbacUserDetails(principal,
                                Set.of(new Role("USER", Set.of(Permission.of("read")))),
                                Set.of()))
                .run(context -> {
                    assertThat(context).hasSingleBean(RbacContextFilter.class);
                    assertThat(context).hasSingleBean(RbacEnforcementAspect.class);
                });
    }

    @Test
    void shouldDisableWhenPropertySetToFalse() {
        contextRunner
                .withPropertyValues("rbac.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(RbacEnforcementAspect.class);
                    assertThat(context).doesNotHaveBean(RbacContextFilter.class);
                });
    }
}
