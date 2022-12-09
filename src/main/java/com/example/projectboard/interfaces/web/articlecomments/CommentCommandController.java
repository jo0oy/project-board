package com.example.projectboard.interfaces.web.articlecomments;

import com.example.projectboard.application.articlecomments.ArticleCommentCommandService;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDtoMapper;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.security.PrincipalUserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@SessionAttributes("article")
@PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})") // 댓글 (등록/수정/삭제)는 인증된 'USER/ADMIN' 사용자만 요청 가능하다.
@RequestMapping("/article-comments")
@Controller
public class CommentCommandController {

    private final ArticleCommentCommandService commentCommandService;
    private final ArticleCommentDtoMapper commentDtoMapper;


    @PostMapping("")
    public String registerComment(@AuthenticationPrincipal PrincipalUserAccount userAccount,
                                  @ModelAttribute("article") ArticleDto.ArticleWithCommentsResponse article,
                                  @Valid @ModelAttribute("registerForm") ArticleCommentDto.RegisterForm registerForm,
                                  BindingResult bindingResult,
                                  SessionStatus sessionStatus) {

        if (bindingResult.hasErrors()) {
            log.debug("errors={}", bindingResult);
            return "articles/detail";
        }

        commentCommandService.registerComment(userAccount.getUsername(), commentDtoMapper.toCommand(registerForm));
        sessionStatus.setComplete(); // 세션에 저장된 "article" 입력 정보 초기화.
        return "redirect:/articles/" + registerForm.getParentArticleId();
    }

    @PutMapping("/{id}")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalUserAccount userAccount,
                         ArticleCommentDto.UpdateForm updateForm) {

        commentCommandService.update(id, userAccount.getUsername(), commentDtoMapper.toCommand(updateForm));

        return "redirect:/articles/" + updateForm.getParentArticleId();
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalUserAccount userAccount,
                         Long articleId) {

        commentCommandService.delete(id, userAccount.getUsername());

        return "redirect:/articles/" + articleId;
    }
}
