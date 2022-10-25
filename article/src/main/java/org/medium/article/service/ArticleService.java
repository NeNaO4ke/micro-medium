package org.medium.article.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.medium.article.domain.Article;
import org.medium.article.domain.ArticlesWithUser;
import org.medium.article.domain.UserDTO;
import org.medium.article.feign.UserService;
import org.medium.article.repository.ArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArticleService {

    @Autowired
    private UserService userService;

    private final ArticleRepository articleRepository;

    public Mono<Article> createArticle(Article article){
        article.setId(null);
        article.setUpvotes(0);
        article.setViewsCount(0);
        return articleRepository.save(article);
    }

    public Mono<Article> getArticleById(String id){
        return articleRepository.findById(id);
    }

    public Mono<Void> deleteArticle(String id){
        return articleRepository.deleteById(id);
    }

    public Flux<Article> getPageArticle(String orderBy, long limit, long offset) {
        return articleRepository.listArticle(orderBy, limit, offset);
    }

    public Mono<ArticlesWithUser> getArticlesWithUser(String orderBy, long limit, long offset){
        Flux<Article> pageArticles = getPageArticle(orderBy, limit, offset).cache();
        Mono<List<String>> listIds = pageArticles.map(Article::getAuthorId)
                .distinct()
                .collectList();
        Flux<UserDTO> usersByIds = listIds.flatMapMany(lst -> userService.getUsersByIds(lst));
        return Mono.zip(pageArticles.collectList(), usersByIds.collectList(), ArticlesWithUser::new);
    }
}
