package jp.i432kg.footprint.presentation.api.request;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

abstract class RequestValidationTestSupport {

    private static final ValidatorFactory VALIDATOR_FACTORY = Validation.buildDefaultValidatorFactory();

    protected static final Validator VALIDATOR = VALIDATOR_FACTORY.getValidator();

    protected static void assertNoViolations(final Set<? extends ConstraintViolation<?>> violations) {
        assertThat(violations).isEmpty();
    }

    protected static void assertViolationsOnlyForProperty(
            final Set<? extends ConstraintViolation<?>> violations,
            final String propertyPath
    ) {
        assertThat(violations)
                .isNotEmpty()
                .extracting(violation -> violation.getPropertyPath().toString())
                .containsOnly(propertyPath);
    }

}
