package org.medium.event.controller;

import lombok.RequiredArgsConstructor;
import org.medium.event.domain.Article;
import org.medium.event.domain.ArticleUpvote;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.CloseStatus;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.concurrent.CopyOnWriteArrayList;

@Component
@RequiredArgsConstructor
public class KafkaWebSocketHandler implements WebSocketHandler {


    private final Sinks.Many<Article> sinkWebProducer;
    private final CopyOnWriteArrayList<String> webSocketSessions;

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        final String sessionId = session.getId();
        HttpHeaders headers = session.getHandshakeInfo().getHeaders();
        String userId = headers.getFirst("X-auth-user-id");
        if(userId == null)
            return session.close(CloseStatus.BAD_DATA);

        webSocketSessions.add(sessionId);

        Logger logger = LoggerFactory.getLogger(EventController.class);

        Flux<WebSocketMessage> output = sinkWebProducer
                .asFlux()
                .map(article -> session.textMessage(article.toString()));

        Mono<Void> closeSession = session.receive()
                .doFinally(sig -> {
                    logger.info("Terminating WebSocket Session (client side) sig: [{}], [{}]", sig.name(), sessionId);
                    webSocketSessions.remove(sessionId);
                }).then(session.close());

        return Mono.zip(session.send(output), closeSession).then();
    }
}
