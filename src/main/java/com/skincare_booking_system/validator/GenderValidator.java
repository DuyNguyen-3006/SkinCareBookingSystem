package com.skincare_booking_system.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GenderValidator implements ConstraintValidator<GenderConstraint, String> {

    private Set<String> validGenders;

    @Override
    public void initialize(GenderConstraint constraintAnnotation) {
        // Convert the annotation's values into a Set for quick lookup
        validGenders = new HashSet<>(Arrays.asList(constraintAnnotation.value()));
    }

    @Override
    public boolean isValid(String gender, ConstraintValidatorContext context) {
        if (gender == null) {
            return true; // Allow null (use @NotNull separately if needed)
        }
        return validGenders.contains(gender);
    }
}
