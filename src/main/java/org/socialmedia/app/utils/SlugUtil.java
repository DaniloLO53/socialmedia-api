package org.socialmedia.app.utils;

import org.socialmedia.app.exceptions.BadRequestException;

import java.util.Locale;
import java.util.regex.Pattern;

public final class SlugUtil {
    private static final Pattern VALID_SLUG_PATTERN = Pattern.compile("^[a-z0-9_]+$");

    public static String validateAndSanitizeName(String input) {
        if (input == null || input.trim().isEmpty()) {
            throw new BadRequestException("O nome do node não pode ser vazio.");
        }

        // Padroniza a entrada para minúsculas para a validação
        String lowercasedInput = input.toLowerCase(Locale.ROOT);

        if (!VALID_SLUG_PATTERN.matcher(lowercasedInput).matches()) {
            // 3. Se não corresponder, lança uma exceção com uma mensagem clara
            throw new BadRequestException(
                "O nome do node é inválido. Use apenas letras (a-z), números (0-9) e underscores (_). Espaços e outros caracteres não são permitidos."
            );
        }

        return lowercasedInput;
    }
}