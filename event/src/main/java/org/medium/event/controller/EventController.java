package org.medium.event.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.medium.event.domain.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.TextMessage;
import java.util.function.Consumer;

//@Controller
@RequiredArgsConstructor
@EnableJms
@Slf4j
@Component
public class EventController {

    private final JmsTemplate jmsTemplate;

    private final Sinks.Many<Article> articleJmsPubSubSink;
    Logger logger = LoggerFactory.getLogger(EventController.class);

    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    @Qualifier("kafka")
    private Sinks.Many<Article> sinkKafkaWebProducer;

    @Bean
    public Consumer<Flux<String>> articleCreated() {
        return flux ->
                flux.map(s -> {
                            Article article;
                            try {
                                article = objectMapper.readValue(s, Article.class);
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            return article;
                        })
                        .map(sinkKafkaWebProducer::tryEmitNext)
                        .subscribe(s -> log.warn("Received: {}", s));
    }

    @JmsListener(destination = "articleCreated", containerFactory = "topicConnectionFactory")
    public void articleCreated(@Payload Message message){
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
    }

    //Example of alternative usage
    @JmsListener(destination = "articleUpvote", selector = "toUserId = '3e908fad-5ded-4f05-ae41-d50530649400'")
    public void articleUpvote(@Payload Message message){
        try {
            TextMessage message1 = (TextMessage) message;
            logger.warn(message1.getText());
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
