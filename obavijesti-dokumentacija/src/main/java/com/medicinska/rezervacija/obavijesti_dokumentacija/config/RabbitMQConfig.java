/* package com.medicinska.rezervacija.obavijesti_dokumentacija.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    // Nazivi redova
    public static final String APPOINTMENT_SCHEDULED_QUEUE = "appointment.scheduled.queue";
    public static final String APPOINTMENT_NOTIFICATION_FAILED_QUEUE = "appointment.notification.failed.queue";
    public static final String APPOINTMENT_NOTIFICATION_CREATED_QUEUE = "appointment.notification.created.queue";


    // Konfiguracija ConnectionFactory za RabbitMQ
    @Bean
    public ConnectionFactory connectionFactory() {
        // Obično se ovo čita iz application.properties, npr. spring.rabbitmq.host, spring.rabbitmq.port itd.
        // Ovdje koristimo defaultne vrijednosti, pretpostavljajući lokalni RabbitMQ
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory("localhost");
        connectionFactory.setUsername("guest"); // Vaši RabbitMQ korisnički podaci
        connectionFactory.setPassword("guest");
        return connectionFactory;
    }

    // Bean za konverziju poruka u JSON format (Jackson)
    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate za slanje poruka
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    // Definicija redova
    @Bean
    public Queue appointmentScheduledQueue() {
        return new Queue(APPOINTMENT_SCHEDULED_QUEUE, true); // durable: true - red preživljava restart brokera
    }

    @Bean
    public Queue appointmentNotificationFailedQueue() {
        return new Queue(APPOINTMENT_NOTIFICATION_FAILED_QUEUE, true);
    }

    @Bean
    public Queue appointmentNotificationCreatedQueue() {
        return new Queue(APPOINTMENT_NOTIFICATION_CREATED_QUEUE, true);
    }
}

 */