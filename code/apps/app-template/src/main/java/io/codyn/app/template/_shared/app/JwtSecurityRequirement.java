package io.codyn.app.template._shared.app;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@SecurityRequirement(name = "bearer-key")
@Inherited
public @interface JwtSecurityRequirement {
}