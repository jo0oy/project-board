package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.application.articles.ArticleCommandService;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ArticleCommandController {

    private final ArticleCommandService articleCommandService;
    private final ArticleDtoMapper articleDtoMapper;

    @PostMapping("/articles")
    public String registerArticle(ArticleDto.RegisterReq req) {
        articleCommandService.registerArticle(articleDtoMapper.toCommand(req));

        return "redirect:/articles";
    }

    @PutMapping("/articles/{id}")
    public String updateArticle(@PathVariable Long id,
                                ArticleDto.UpdateReq req) {

        articleCommandService.update(id, articleDtoMapper.toCommand(req));

        return "redirect:/articles/" + id;
    }

    @DeleteMapping("/articles/{id}")
    public String deleteArticle(@PathVariable Long id) {

        articleCommandService.delete(id);

        return "redirect:/articles";
    }
}
