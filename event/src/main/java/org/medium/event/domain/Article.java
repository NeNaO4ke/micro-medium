package org.medium.event.domain;

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
}
