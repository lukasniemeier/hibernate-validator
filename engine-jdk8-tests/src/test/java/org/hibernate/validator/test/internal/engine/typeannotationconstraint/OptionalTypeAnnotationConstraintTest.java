package org.hibernate.validator.test.internal.engine.typeannotationconstraint;

import org.hibernate.validator.constraints.CompositionType;
import org.hibernate.validator.constraints.ConstraintComposition;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.testutil.constraints.NotNullTypeUse;
import org.hibernate.validator.valuehandling.UnwrapValidatedValue;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.validation.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Optional;
import java.util.Set;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.hibernate.validator.testutil.ConstraintViolationAssert.*;
import static org.hibernate.validator.testutil.ValidatorUtil.getValidator;

/**
 */
public class OptionalTypeAnnotationConstraintTest {

    private static class Model {

        @UnwrapValidatedValue(false)
        @NotNull(message = "container")
        Optional<String> valueWithoutTypeAnnotation;

        @UnwrapValidatedValue(false)
        @NotNull(message = "container")
        Optional<@NotNullTypeUse(message = "type") String> valueWithNotNull;

        @UnwrapValidatedValue(false)
        @NotNull(message = "container")
        Optional<@NullOrNotBlank(message = "type") String> valueWithNullOrNotBlank;

        @NullOrNotBlank(message = "reference")
        String valueReference;

    }

    private Validator validator;

    @BeforeClass
    public void setup() {
        validator = getValidator();
    }


    @Test
    public void without_type_annotation_on_optional_is_validated_for_null_value() {
        Model model = new Model();
        model.valueWithoutTypeAnnotation = null;

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueWithoutTypeAnnotation");
        assertNumberOfViolations( constraintViolations, 1 );
        assertCorrectPropertyPaths( constraintViolations, "valueWithoutTypeAnnotation" );
        assertConstraintViolation(constraintViolations.iterator().next(), "container");
        assertCorrectConstraintTypes(constraintViolations, NotNull.class);
    }

    @Test
    public void without_type_annotation_on_optional_is_validated_for_empty_value() {
        Model model = new Model();
        model.valueWithoutTypeAnnotation = Optional.empty();

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueWithoutTypeAnnotation");
        assertNumberOfViolations( constraintViolations, 0 );
    }

    /**
     * Actual: Fails with NullPointerException (Unexpected exception during isValid call)
     */
    @Test
    public void not_null_type_on_optional_is_validated_for_null_value() {
        Model model = new Model();
        model.valueWithNotNull = null;

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueWithNotNull");
        assertNumberOfViolations( constraintViolations, 1 );
        assertCorrectPropertyPaths( constraintViolations, "valueWithNotNull" );
        assertConstraintViolation(constraintViolations.iterator().next(), "container");
        assertCorrectConstraintTypes(constraintViolations, NotNull.class);
    }

    /**
     * Actual: NotNull violation occurs on container's annotation
     */
    @Test
    public void not_null_type_on_optional_is_validated_for_empty_value() {
        Model model = new Model();
        model.valueWithNotNull = Optional.empty();

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueWithNotNull");
        assertNumberOfViolations( constraintViolations, 1 );
        assertCorrectPropertyPaths( constraintViolations, "valueWithNotNull" );
        assertConstraintViolation(constraintViolations.iterator().next(), "type");
        assertCorrectConstraintTypes(constraintViolations, NotNullTypeUse.class);
    }

    /**
     * Actual: Fails with NullPointerException (Unexpected exception during isValid call)
     */
    @Test
    public void null_or_not_blank_type_on_optional_is_validated_for_null_value() {
        Model model = new Model();
        model.valueWithNullOrNotBlank = null;

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueWithNullOrNotBlank");
        assertNumberOfViolations( constraintViolations, 1 );
        assertCorrectPropertyPaths( constraintViolations, "valueWithNullOrNotBlank" );
        assertConstraintViolation(constraintViolations.iterator().next(), "container");
        assertCorrectConstraintTypes(constraintViolations, NotNull.class);
    }

    /**
     * Actual: NotNull violation occurs on container's annotation
     */
    @Test
    public void null_or_not_blank_type_on_optional_is_validated_for_empty_value() {
        Model model = new Model();
        model.valueWithNullOrNotBlank = Optional.empty();

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueWithNullOrNotBlank");
        assertNumberOfViolations(constraintViolations, 0);
    }

    /**
     * Actual: No violation
     */
    @Test
    public void null_or_not_blank_type_on_optional_is_validated_for_blank_string() {
        Model model = new Model();
        model.valueWithNullOrNotBlank = Optional.of("");

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueWithNullOrNotBlank");
        assertNumberOfViolations( constraintViolations, 1 );
        assertCorrectPropertyPaths( constraintViolations, "valueWithNullOrNotBlank" );
        assertConstraintViolation(constraintViolations.iterator().next(), "type");
        assertCorrectConstraintTypes(constraintViolations, NullOrNotBlank.class);
    }

    @Test
    public void reference_is_validated_for_null_value() {
        Model model = new Model();
        model.valueReference = null;

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueReference");
        assertNumberOfViolations(constraintViolations, 0);
    }

    @Test
    public void reference_is_validated_for_empty_string() {
        Model model = new Model();
        model.valueReference = "";

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueReference");
        assertNumberOfViolations(constraintViolations, 1);
        assertCorrectPropertyPaths( constraintViolations, "valueReference" );
        assertConstraintViolation(constraintViolations.iterator().next(), "reference");
        assertCorrectConstraintTypes(constraintViolations, NullOrNotBlank.class);
    }

    @Test
    public void reference_is_validated_for_valid_string() {
        Model model = new Model();
        model.valueReference = "1";

        Set<ConstraintViolation<Model>> constraintViolations = validator.validateProperty(model, "valueReference");
        assertNumberOfViolations(constraintViolations, 0);
    }
    
    @ConstraintComposition(CompositionType.OR)
    @Null
    @NotBlank
    @ReportAsSingleViolation
    @Constraint(validatedBy = { })
    @Target({ TYPE_USE, METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface NullOrNotBlank {

        String message() default "NullOrNotBlank";

        Class<?>[] groups() default { };

        Class<? extends Payload>[] payload() default { };
    }
}
