package org.medium.article.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.medium.article.domain.Article;
import org.medium.article.domain.ArticleUpvote;
import org.medium.article.domain.ArticlesWithUser;
import org.medium.article.domain.UserDTO;
import org.medium.article.feign.UserService;
import org.medium.article.repository.ArticleRepository;
import org.medium.article.repository.ArticleUpvoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.destination.JmsDestinationAccessor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import javax.jms.Destination;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    @Autowired
    private UserService userService;

    private final ArticleRepository articleRepository;
    private final ArticleUpvoteRepository articleUpvoteRepository;
    private final JmsTemplate jmsTemplate;
    private final JmsTemplate topicJmsTemplate;

    private final KafkaTemplate<String, byte[]> kafkaTemplate;

    ObjectMapper objectMapper = new ObjectMapper();

    public Mono<Article> createArticle(Article article) {
        article.setId(null);
        article.setUpvotes(0);
        article.setViewsCount(0);
        Mono<Void> sendMessageMono = Mono.defer(() -> {
            topicJmsTemplate.convertAndSend("articleCreated", article);
            try {
                kafkaTemplate.send("articleCreated", objectMapper.writeValueAsString(article).getBytes());
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            return Mono.empty();
        });
        return Mono.zip(articleRepository.save(article), sendMessageMono).flatMap(objects -> Mono.just(objects.getT1()));
    }

    public Mono<Article> getArticleById(String id) {
        return articleRepository.findById(id);
    }

    public Mono<?> upvote(String userId, String articleId) {
        String upvoteId = String.format("%s.%s", articleId, userId);
        return articleUpvoteRepository.existsById(upvoteId)
                .flatMap(exists -> {
                    if (exists) {
                        return articleRepository.findById(articleId)
                                .flatMap(article -> {
                                    long upvotes = article.getUpvotes();
                                    article.setUpvotes(--upvotes);
                                    return Mono.zip(articleRepository.save(article), articleUpvoteRepository.deleteById(upvoteId))
                                            .map(Tuple2::getT1);
                                });
                    } else {
                        return Mono.zip(articleUpvoteRepository.save(new ArticleUpvote(upvoteId, new Date())), articleRepository.findById(articleId))
                                .flatMap(objects -> {
                                    Article article = objects.getT2();
                                    long upvotes = article.getUpvotes();
                                    article.setUpvotes(++upvotes);
                                    jmsTemplate.convertAndSend("articleUpvote", objects.getT1(), message -> {
                                        message.setStringProperty("toUserId", article.getAuthorId());
                                        return message;
                                    });

                                    return articleRepository.save(article);
                                });
                    }
                });
    }

    public Mono<Void> deleteArticle(String id) {
        return articleRepository.deleteById(id);
    }

    public Flux<Article> getPageArticle(String orderBy, long limit, long offset) {
        return Mono.defer(() -> {
            jmsTemplate.convertAndSend("test", "Hello it`s me, Mario!");
            return Mono.empty();
        }).thenMany(articleRepository.listArticle(orderBy, limit, offset));
    }

    public Mono<ArticlesWithUser> getArticlesWithUser(String orderBy, long limit, long offset) {
        Flux<Article> pageArticles = getPageArticle(orderBy, limit, offset).cache();
        Mono<List<String>> listIds = pageArticles.map(Article::getAuthorId)
                .distinct()
                .collectList();
        Flux<UserDTO> usersByIds = listIds.flatMapMany(lst -> userService.getUsersByIds(lst));
        return Mono.zip(pageArticles.collectList(), usersByIds.collectList(), ArticlesWithUser::new);
    }
}
