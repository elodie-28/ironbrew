package dev.elodie.ironbrew.asset.collection;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Pattern;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.TYPE_USE })
@Pattern(
        regexp = "^asset_collection:((?:[a-z][a-z0-9_]*|\\*)(?:\\/(?:[a-z][a-z0-9_]*|\\*|\\*\\*)){1,4})$"
)
@Constraint(validatedBy = { })
@Retention(RetentionPolicy.RUNTIME)
public @interface AssetCollectionIdWildcard {
    String message() default "Invalid asset collection path pattern";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
