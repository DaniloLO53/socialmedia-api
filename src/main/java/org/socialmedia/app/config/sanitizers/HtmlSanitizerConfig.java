package org.socialmedia.app.config.sanitizers;

import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;

import java.util.regex.Pattern;

public class HtmlSanitizerConfig {

    public static final int MAX_INPUT_SIZE_BYTES = 100_000;
    private static final PolicyFactory SECURE_POLICY = createSecurePolicy();

    private static PolicyFactory createSecurePolicy() {
        return new HtmlPolicyBuilder()
                .allowUrlProtocols("https", "http")
                .allowElements("a")
                .allowAttributes("href", "target").onElements("a")
                // Isto garante que o atributo 'target' só pode ter o valor EXATO "_blank"
                .allowAttributes("target").matching(Pattern.compile("^_blank$")).onElements("a")
                .requireRelNofollowOnLinks()
                .requireRelsOnLinks("noopener", "noreferrer")
                .allowElements("p", "b", "i", "u", "strong", "em", "br")
                .allowElements("ol", "ul", "li")
                .allowElements("blockquote")
                .allowAttributes("cite").onElements("blockquote")
                .toFactory();
    }

    public static String sanitize(String dirtyHtml) {
        if (dirtyHtml == null || dirtyHtml.isEmpty()) {
            return dirtyHtml;
        }

        // A validação de tamanho é feita manualmente antes
        if (dirtyHtml.getBytes().length > MAX_INPUT_SIZE_BYTES) {
            throw new IllegalArgumentException("O conteúdo excede o tamanho máximo permitido.");
        }

        return SECURE_POLICY.sanitize(dirtyHtml);
    }
}