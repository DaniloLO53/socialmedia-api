package org.socialmedia.app.validations.allowedVoteDirections;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.List;

public class AllowedVoteDirectionsValidator implements ConstraintValidator<AllowedVoteDirections, Integer> {

    // A lista de valores que são permitidos
    private static final List<Integer> ALLOWED_VALUES = Arrays.asList(-1, 1);

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        // Se o valor for nulo, a validação @NotNull deve cuidar disso.
        // Aqui, consideramos nulo como válido para não duplicar a responsabilidade.
        if (value == null) {
            return true;
        }

        // A validação real: verifica se o valor está na nossa lista de permitidos.
        return ALLOWED_VALUES.contains(value);
    }
}
