package com.example.projectboard.interfaces.web.articles;

import com.example.projectboard.application.articles.ArticleCommandService;
import com.example.projectboard.common.util.HashtagContentUtils;
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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/articles")
@Controller
public class ArticleCommandController {

    private final ArticleCommandService articleCommandService;
    private final ArticleDtoMapper articleDtoMapper;

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PostMapping("/new")
    public String registerArticle(@AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                  @Valid @ModelAttribute("registerForm") ArticleDto.RegisterForm form,
                                  BindingResult bindingResult) {

        // hashtagContent 검증 로직 실행
        var hashtagNames = HashtagContentUtils.convertToList(form.getHashtagContent());

        verifyHashtagContent(hashtagNames, bindingResult);

        // 검증 오류 발생시 이전 뷰에 오류 담아서 반환.
        if (bindingResult.hasErrors()) {
            log.debug("errors={}", bindingResult);
            return "articles/add-form";
        }

        articleCommandService.registerArticleWithValidHashtags(principalUserAccount.getUsername(),
                articleDtoMapper.toCommand(form, hashtagNames));

        return "redirect:/articles";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/{id}/edit")
    public String updateArticle(@PathVariable Long id,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                @Valid @ModelAttribute("updateForm") ArticleDto.UpdateForm updateForm,
                                BindingResult bindingResult) {

        // hashtagContent 검증 로직 실행
        var hashtagNames = HashtagContentUtils.convertToList(updateForm.getHashtagContent());

        verifyHashtagContent(hashtagNames, bindingResult);

        // 검증 오류 발생시 이전 뷰에 오류 담아서 반환.
        if (bindingResult.hasErrors()) {
            log.debug("errors={}", bindingResult);
            return "articles/edit-form";
        }

        articleCommandService.update(id, principalUserAccount.getUsername(), articleDtoMapper.toCommand(updateForm, hashtagNames));

        return "redirect:/articles/" + id;
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @DeleteMapping("/{id}")
    public String deleteArticle(@PathVariable Long id,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        articleCommandService.delete(id, principalUserAccount.getUsername());

        return "redirect:/articles";
    }

    private void verifyHashtagContent(List<String> hashtagNames, BindingResult bindingResult) {
        if (HashtagContentUtils.verifyFormat(hashtagNames)) {
            bindingResult.rejectValue("hashtagContent", "Format");
            log.debug("errors={}", bindingResult);
        }

        if (HashtagContentUtils.verifyHashtagSize(hashtagNames)) {
            bindingResult.rejectValue("hashtagContent", "Size");
            log.debug("errors={}", bindingResult);
        }
    }
}
