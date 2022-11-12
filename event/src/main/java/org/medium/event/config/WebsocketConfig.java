package org.medium.event.config;

import lombok.RequiredArgsConstructor;
import org.medium.event.controller.JmsPubSubWebSocketHandler;
import org.medium.event.controller.JmsQueueWebSocketHandler;
import org.medium.event.controller.KafkaWebSocketHandler;
import org.medium.event.domain.Article;
import org.medium.event.domain.ArticleUpvote;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.AbstractJmsListenerContainerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;


@Configuration
@RequiredArgsConstructor
public class WebsocketConfig {

    private final Sinks.Many<Article> sinkWebProducer;
    private final AbstractJmsListenerContainerFactory<?> jmsListenerContainerFactory;
    private final Sinks.Many<Article> articleJmsPubSubSink;
    private final MessageConverter jacksonJmsMessageConverter;
    private final Flux<Article> articleJmsPubSubFlux;

    private final CopyOnWriteArrayList<String> webSocketSessions;

    @Bean
    public HandlerMapping handlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/kafka/article-created",
                new KafkaWebSocketHandler(sinkWebProducer, webSocketSessions));
        map.put("/ws/jms/q/article-upvote",
                new JmsQueueWebSocketHandler(jmsListenerContainerFactory, webSocketSessions, jacksonJmsMessageConverter));
        map.put("/ws/jms/q/article-created",
                new JmsPubSubWebSocketHandler(articleJmsPubSubFlux, webSocketSessions));
        int order = -1; // before annotated controllers

        return new SimpleUrlHandlerMapping(map, order);
    }

}
