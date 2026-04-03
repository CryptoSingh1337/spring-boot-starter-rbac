package pro.saransh.springboot.rbac.core;

import java.util.Objects;

public record Permission(String value) {

    public Permission {
        Objects.requireNonNull(value, "Permission value must not be null");
        if (value.isBlank()) {
            throw new IllegalArgumentException("Permission value must not be blank");
        }
    }

    public static Permission of(String value) {
        return new Permission(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
