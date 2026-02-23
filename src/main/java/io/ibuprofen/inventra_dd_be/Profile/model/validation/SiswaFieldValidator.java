package io.ibuprofen.inventra_dd_be.Profile.model.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import io.ibuprofen.inventra_dd_be.Profile.model.User;
import io.ibuprofen.inventra_dd_be.Profile.model.Role;

public class SiswaFieldValidator implements ConstraintValidator<ValidateSiswaFields, User> {

    @Override
    public void initialize(ValidateSiswaFields constraintAnnotation) {
    }

    @Override
    public boolean isValid(User user, ConstraintValidatorContext context) {
        if (user == null) {
            return true;
        }

        boolean isSiswa = user.getRole() == Role.SISWA;

        // Jika SISWA, NISN dan Kelas harus ada (not blank)
        if (isSiswa) {
            if (user.getNisn() == null || user.getNisn().isBlank()) {
                addConstraintViolation(context, "NISN tidak boleh kosong untuk role SISWA");
                return false;
            }
            if (!user.getNisn().matches("^[0-9]+$")) {
                addConstraintViolation(context, "NISN harus hanya berisi angka");
                return false;
            }
            if (user.getKelas() == null || user.getKelas().isBlank()) {
                addConstraintViolation(context, "Kelas tidak boleh kosong untuk role SISWA");
                return false;
            }
        } else {
            // Jika bukan SISWA, NISN dan Kelas harus null
            if (user.getNisn() != null || user.getKelas() != null) {
                addConstraintViolation(context, "NISN dan Kelas hanya untuk role SISWA");
                return false;
            }
        }

        return true;
    }

    private void addConstraintViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message)
                .addConstraintViolation();
    }
}
