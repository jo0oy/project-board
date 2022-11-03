package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.application.PaginationService;
import com.example.projectboard.application.articles.ArticleQueryService;
import com.example.projectboard.domain.articles.SearchType;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import com.example.projectboard.security.PrincipalUserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@SessionAttributes("article")
@RequestMapping("/articles")
@Controller
public class ArticleViewController {

    private final ArticleQueryService articleQueryService;
    private final PaginationService paginationService;
    private final ArticleDtoMapper articleDtoMapper;

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @GetMapping("/new")
    public String addArticleForm(Model model) {

        var registerForm = ArticleDto.RegisterForm.builder().build();
        model.addAttribute("registerForm", registerForm);

        return "articles/add-form";
    }

    @GetMapping
    public String articles(@RequestParam(required = false) SearchType searchType,
                           @RequestParam(required = false) String searchValue,
                           @PageableDefault(size = 15, direction = Sort.Direction.DESC, sort = "createdAt") Pageable pageable,
                           Model model) {

        var searchCondition = articleDtoMapper.toCommand(ArticleDto.SearchCondition.of(searchType, searchValue));
        var articles = articleQueryService.articles(searchCondition, pageable).map(articleDtoMapper::toDto);
        var barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        model.addAttribute("articles", articles);
        model.addAttribute("paginationBar", barNumbers);
        model.addAttribute("searchTypes", SearchType.values());

        return "articles/index";
    }

    @GetMapping("/hashtag-search/{hashtagId}")
    public String articlesByHashtag(@PathVariable Long hashtagId,
                                    @PageableDefault(size = 15, direction = Sort.Direction.DESC, sort = "article.createdAt") Pageable pageable,
                                    Model model) {

        var articles = articleQueryService.articlesByHashtagId(hashtagId, pageable).map(articleDtoMapper::toDto);
        var barNumbers = paginationService.getPaginationBarNumbers(pageable.getPageNumber(), articles.getTotalPages());

        model.addAttribute("articles", articles);
        model.addAttribute("paginationBar", barNumbers);

        return "articles/hashtag-search";
    }

    @GetMapping("/{id}")
    public String articleDetail(@PathVariable Long id,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                Model model) {

        var article
                = articleDtoMapper.toDto(articleQueryService.getArticleWithComments(id, (principalUserAccount != null) ? principalUserAccount.getUsername() : null));
        var registerForm = ArticleCommentDto.RegisterForm.builder().parentArticleId(id).build();

        model.addAttribute("article", article);
        model.addAttribute("registerForm", registerForm);

        return "articles/detail";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @GetMapping("/{id}/edit")
    public String editArticle(@PathVariable Long id, Model model) {

        var article = articleQueryService.getArticle(id);
        var updateForm = articleDtoMapper.toFormDto(article);

        model.addAttribute("updateForm", updateForm);

        return "articles/edit-form";
    }
}
