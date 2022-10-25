package org.medium.article.domain;

import lombok.Value;

import java.util.List;

@Value
public class ArticlesWithUser {
    List<Article> article;
    List<UserDTO> userDTO;
}
