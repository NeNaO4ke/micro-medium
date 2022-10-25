package org.medium.article.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.couchbase.core.index.CompositeQueryIndex;
import org.springframework.data.couchbase.core.index.QueryIndexed;
import org.springframework.data.couchbase.core.mapping.Document;
import org.springframework.data.couchbase.core.mapping.Field;
import org.springframework.data.couchbase.core.mapping.id.GeneratedValue;

import java.util.HashSet;
import java.util.Set;

import static org.springframework.data.couchbase.core.mapping.id.GenerationStrategy.UNIQUE;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
@CompositeQueryIndex(fields = {"_class"})
public class Article {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Id @GeneratedValue(strategy = UNIQUE)
    private String id;
    @Field
    private String title;
    @Field @QueryIndexed
    private String authorId;
    @Field
    private String text;
    @Field
    private Set<String> attachments = new HashSet<>();
    @Field
    private Set<String> tags = new HashSet<>();
    @Field @QueryIndexed
    private long upvotes;
    @Field @QueryIndexed
    private long viewsCount;

//    public Article(String title, String authorId, String text, Set<String> attachments, Set<String> tags){
//        this.title = title;
//        this.authorId = authorId;
//        this.text = text;
//        this.attachments = attachments;
//        this.tags = tags;
//        this.upvotes = 0;
//        this.viewsCount = 0;
//    }

}
