package org.medium.article.controller;

import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.medium.article.domain.Article;
import org.medium.article.service.ArticleService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/article")
public class ArticlePageController {

    private final ArticleService articleService;

    @GetMapping("/{id}")
    public String getArticle(@PathVariable("id") String id, Model model) {
        Mono<Article> article = articleService.getArticleById(id);
        model.addAttribute("article", article);
        return "article";
    }
}
