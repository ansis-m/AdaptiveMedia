package com.adaptivemedia.assignment.web.validators;

import com.adaptivemedia.assignment.properties.TrackingProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TrackingCodeValidator implements ConstraintValidator<ValidTrackingCode, String> {

    private final TrackingProperties trackingProperties;

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && trackingProperties.getCodes().contains(value);
    }
}
