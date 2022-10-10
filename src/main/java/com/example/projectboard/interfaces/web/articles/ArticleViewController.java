package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.application.PaginationService;
import com.example.projectboard.application.articles.ArticleQueryService;
import com.example.projectboard.domain.articles.SearchType;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleViewController {

    private final ArticleQueryService articleQueryService;
    private final PaginationService paginationService;
    private final ArticleDtoMapper articleDtoMapper;

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @GetMapping("/new")
    public String addArticleForm() {
        return "articles/add-form";
    }

    @GetMapping
    public String articles(Model model,
                           @RequestParam(required = false) SearchType searchType,
                           @RequestParam(required = false) String searchValue,
                           @PageableDefault(size = 15, direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable) {

        var searchCondition = articleDtoMapper.toCommand(ArticleDto.SearchCondition.of(searchType, searchValue));
        var articles = articleQueryService.articles(searchCondition, pageable).map(articleDtoMapper::toDto);

        var barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());
        model.addAttribute("articles", articles);
        model.addAttribute("paginationBar", barNumbers);
        model.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @GetMapping("/{id}")
    public String articleDetail(@PathVariable Long id, Model model) {

        var article = articleDtoMapper.toDto(articleQueryService.getArticleWithComments(id));
        model.addAttribute("article", article);
        return "articles/detail";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @GetMapping("/{id}/edit")
    public String editArticle(@PathVariable Long id, Model model) {

        var article = articleDtoMapper.toDto(articleQueryService.getArticle(id));
        model.addAttribute("article", article);
        return "articles/edit-form";
    }
}
