package org.medium.article.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;
import org.springframework.data.couchbase.core.mapping.id.GenerationStrategy;

import java.util.Date;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleView {

    @Id
    @GeneratedValue(strategy = GenerationStrategy.USE_ATTRIBUTES)
    @QueryIndexed
    private String id;

    @Field
    private String articleId;

    @Field
    private String userId;

    @Field
    private Date viewedAt;
}
