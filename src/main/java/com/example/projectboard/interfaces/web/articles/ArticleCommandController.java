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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
public class ArticleCommandController {

    private final ArticleCommandService articleCommandService;
    private final ArticleDtoMapper articleDtoMapper;

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PostMapping("/articles/new")
    public String registerArticle(@AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                  @Valid @ModelAttribute("registerForm") ArticleDto.RegisterForm form,
                                  BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.debug("errors={}", bindingResult);
            return "articles/add-form";
        }

        articleCommandService.registerArticle(principalUserAccount.getUsername(), articleDtoMapper.toCommand(form));

        return "redirect:/articles";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/articles/{id}/edit")
    public String updateArticle(@PathVariable Long id,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                @Valid @ModelAttribute("updateForm") ArticleDto.UpdateForm updateForm,
                                BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            log.debug("errors={}", bindingResult);
            return "articles/edit-form";
        }

        articleCommandService.update(id, principalUserAccount.getUsername(), articleDtoMapper.toCommand(updateForm));

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
