package com.skincare_booking_system.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ FIELD })
@Retention(RUNTIME)
@Constraint(validatedBy = {GenderValidator.class})
public @interface GenderConstraint {
    String message() default "Invalid gender";

    // Default values for valid genders
    String[] value() default { "Male", "Female", "Other" };

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
