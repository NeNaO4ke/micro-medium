package org.medium.gateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Article {
    private String id;
    private String title;
    private String authorId;
    private String text;
    private Set<String> attachments = new HashSet<>();
    private Set<String> tags = new HashSet<>();
    private long upvotes;
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
