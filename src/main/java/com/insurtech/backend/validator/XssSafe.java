package com.insurtech.backend.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = XssSafeValidator.class)
public @interface XssSafe {
  String message() default "Input contains unsafe HTML content";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
