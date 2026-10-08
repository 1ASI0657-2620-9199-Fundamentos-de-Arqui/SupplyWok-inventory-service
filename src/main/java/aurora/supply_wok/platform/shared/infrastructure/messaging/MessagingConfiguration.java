package aurora.supply_wok.platform.shared.infrastructure.messaging;

import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * RabbitMQ messaging configuration for inventory service.
 */
@Configuration
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
public class MessagingConfiguration {

    public static final String EXCHANGE_NAME = "supplywok.events";

    @Bean
    public TopicExchange eventsExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public MessageConverter messageConverter() {
        return new JacksonJsonMessageConverter("aurora.supply_wok.platform.inventory.interfaces.events");
    }
}
