package org.xuxuchat.app.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
            // Exige que qualquer mensagem enviada de um cliente para um destino
            // no servidor (prefixo /app) seja autenticada.
            .simpDestMatchers("/app/**").authenticated()

            // Exige que qualquer tentativa de inscrição (SUBSCRIBE) seja autenticada.
            // A autorização fina (qual tópico pode ser acessado) é feita no nosso interceptor.
            .simpSubscribeDestMatchers("/topic/**").authenticated()

            // Permite mensagens de conexão e outras mensagens do sistema sem autenticação,
            // pois nossa autenticação é feita manualmente no interceptor no frame CONNECT.
            .simpTypeMatchers(
                SimpMessageType.CONNECT,
                SimpMessageType.HEARTBEAT,
                SimpMessageType.UNSUBSCRIBE,
                SimpMessageType.DISCONNECT
            ).permitAll()

            // Todas as outras mensagens devem ser autenticadas.
            .anyMessage().authenticated();
    }

    /**
     * Desabilita a proteção CSRF para WebSockets. A autenticação baseada em token (JWT)
     * já mitiga os riscos de CSRF para este tipo de comunicação.
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
