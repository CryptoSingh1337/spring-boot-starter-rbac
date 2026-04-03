package pro.saransh.springboot.rbac.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface HasAnyPermission {
    String[] value();
}
