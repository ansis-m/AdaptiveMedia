package com.adaptivemedia.assignment.web.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TrackingCodeValidator.class)
public @interface ValidTrackingCode {
    String message() default "Invalid tracking code";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

