package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.application.articles.ArticleCommandService;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import com.example.projectboard.security.PrincipalUserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PostMapping("/articles")
    public String registerArticle(ArticleDto.RegisterReq req,
                                  @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {
        articleCommandService.registerArticle(principalUserAccount.getUsername(), articleDtoMapper.toCommand(req));

        return "redirect:/articles";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/articles/{id}")
    public String updateArticle(@PathVariable Long id,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                ArticleDto.UpdateReq req) {

        articleCommandService.update(id, principalUserAccount.getUsername(), articleDtoMapper.toCommand(req));

        return "redirect:/articles/" + id;
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @DeleteMapping("/articles/{id}")
    public String deleteArticle(@PathVariable Long id,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        articleCommandService.delete(id, principalUserAccount.getUsername());

        return "redirect:/articles";
    }
}
