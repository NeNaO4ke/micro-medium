package org.medium.article.repository;

import org.medium.article.domain.Article;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleRepository extends ReactiveCouchbaseRepository<Article, String> {

    @Query("#{#n1ql.selectEntity} where #{#n1ql.filter} ORDER BY #{[0]} DESC LIMIT $2 OFFSET $3")
    Flux<Article> listArticle(String orderBy, long limit, long offset);

}
