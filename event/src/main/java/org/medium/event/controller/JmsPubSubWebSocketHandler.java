package org.medium.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.medium.event.domain.Article;
import org.medium.event.domain.ArticleUpvote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.jms.config.AbstractJmsListenerContainerFactory;
import org.springframework.jms.config.SimpleJmsListenerEndpoint;
import org.springframework.jms.listener.AbstractMessageListenerContainer;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.jms.JMSException;
import javax.jms.TextMessage;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JmsPubSubWebSocketHandler implements WebSocketHandler {
  //  private final AbstractJmsListenerContainerFactory<?> jmsListenerContainerFactory;

    private final Flux<Article> articleJmsPubSubFlux;
    private final CopyOnWriteArrayList<String> webSocketSessions;



    @Override
    public Mono<Void> handle(WebSocketSession session) {
        final String sessionId = session.getId();
        HttpHeaders headers = session.getHandshakeInfo().getHeaders();
        String userId = headers.getFirst("X-auth-user-id");
        if (userId == null)
            return session.close(CloseStatus.BAD_DATA);

        webSocketSessions.add(sessionId);

        Logger logger = LoggerFactory.getLogger(EventController.class);

       /* SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
        endpoint.setMessageListener(message -> {
            Article article;
            try {
                logger.warn(message.toString() + " Received");
                String text = ((TextMessage) message).getText();
                article = objectMapper.readValue(text, Article.class);
            } catch (JsonProcessingException | JMSException e) {
                articleJmsPubSubSink.tryEmitError(new JMSException("Error parsing message!"));
                throw new RuntimeException(e);
            }
            articleJmsPubSubSink.tryEmitNext(article);
        });
        AbstractMessageListenerContainer container = jmsListenerContainerFactory.createListenerContainer(endpoint);
        container.setDestinationName("articleCreated");
        container.setPubSubDomain(true);
        container.setSubscriptionDurable(false);
        container.initialize();
        container.start();*/


        Flux<WebSocketMessage> output = articleJmsPubSubFlux.map(articleUpvote -> session.textMessage(articleUpvote.toString()));

        Mono<Void> closeSession = session.receive()
                .doFinally(sig -> {
                    logger.info("Terminating WebSocket Session (client side) sig: [{}], [{}]", sig.name(), sessionId);
                    webSocketSessions.remove(sessionId);
             //       container.shutdown();
                }).then(session.close());

        return Mono.zip(session.send(output), closeSession).then();
    }
}
