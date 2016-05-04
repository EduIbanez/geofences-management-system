package es.unizar.iaaa.geofencing.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                .simpDestMatchers("/api/locations").permitAll()
                .simpSubscribeDestMatchers("/topic/positions").permitAll();
    }

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}
