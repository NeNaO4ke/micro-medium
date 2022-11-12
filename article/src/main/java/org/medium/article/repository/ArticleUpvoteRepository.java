package org.medium.article.repository;

import org.medium.article.domain.Article;
import org.medium.article.domain.ArticleUpvote;
import org.springframework.data.couchbase.repository.Query;
import org.springframework.data.couchbase.repository.ReactiveCouchbaseRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ArticleUpvoteRepository extends ReactiveCouchbaseRepository<ArticleUpvote, String> {


}
