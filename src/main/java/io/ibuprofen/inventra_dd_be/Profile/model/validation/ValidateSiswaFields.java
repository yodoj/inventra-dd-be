package io.ibuprofen.inventra_dd_be.Profile.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SiswaFieldValidator.class)
@Documented
public @interface ValidateSiswaFields {
    String message() default "NISN dan Kelas hanya boleh diisi untuk role SISWA";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
