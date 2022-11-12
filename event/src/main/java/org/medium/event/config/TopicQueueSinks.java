package org.medium.event.config;

import org.medium.event.domain.Article;
import org.medium.event.domain.ArticleUpvote;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.CopyOnWriteArrayList;


@Configuration
public class TopicQueueSinks {

    @Bean
    @Qualifier("jms")
    @Primary
    public Sinks.Many<Article> articleJmsPubSubSink() {
        return Sinks.many().multicast().directBestEffort();
    }

    @Bean
    public Flux<Article> articleJmsPubSubFlux() {
        return articleJmsPubSubSink().asFlux();
    }

    @Bean
    @Qualifier("kafka")
    public Sinks.Many<Article> sinkWebProducer() {
        return Sinks.many().multicast().directBestEffort();
    }

    @Bean
    private static CopyOnWriteArrayList<String> webSocketSessions() {
        return new CopyOnWriteArrayList<>();
    }

}
