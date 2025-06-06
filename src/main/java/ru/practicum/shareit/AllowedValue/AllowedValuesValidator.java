package ru.practicum.shareit.AllowedValue;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Arrays;
import java.util.List;

public class AllowedValuesValidator implements ConstraintValidator<AllowedValues, String> {
    private List<String> allowedValues;

    @Override
    public void initialize(AllowedValues constraintAnnotation) {
        this.allowedValues = Arrays.asList(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value == null || allowedValues.contains(value);
    }
}