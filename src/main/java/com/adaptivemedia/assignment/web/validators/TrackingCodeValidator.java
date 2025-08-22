package com.adaptivemedia.assignment.web.validators;

import com.adaptivemedia.assignment.utils.ApplicationContextHolder;
import com.adaptivemedia.assignment.properties.TrackingProperties;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TrackingCodeValidator implements ConstraintValidator<ValidTrackingCode, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        TrackingProperties trackingProperties = ApplicationContextHolder.getBean(TrackingProperties.class);

        if (trackingProperties == null) {
            log.error("Tracking properties is null");
            return true;
        }

        return value != null && trackingProperties.getCodes().contains(value);
    }
}
