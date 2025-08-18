package org.socialmedia.app.utils;

import java.text.Normalizer;
import java.util.regex.Pattern;

public final class UnicodeNormalizer {

    private static final Pattern DIACRITICALS_AND_FRIENDS =
        Pattern.compile("[\\p{InCombiningDiacriticalMarks}\\p{IsLm}\\p{IsSk}]+");

    /**
     * Normaliza uma string para a forma NFKC, remove acentos e caracteres de controle,
     * e converte para minúsculas. Ideal para identificadores únicos.
     */
    public static String normalizeForComparison(String input) {
        if (input == null || input.trim().isEmpty()) {
            return input;
        }

        // Normaliza para NFKC para unificar caracteres compatíveis (ex: "ﬁ" -> "fi")
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFKC);

        // Remove acentos e diacríticos (marcas de combinação)
        normalized = DIACRITICALS_AND_FRIENDS.matcher(normalized).replaceAll("");

        // Remove caracteres de controle invisíveis
        normalized = normalized.replaceAll("\\p{Cc}", "");

        return normalized.toLowerCase();
    }
}
