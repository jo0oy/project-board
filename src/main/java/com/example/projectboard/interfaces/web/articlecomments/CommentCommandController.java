package com.example.projectboard.interfaces.web.articlecomments;

import com.example.projectboard.application.articlecomments.ArticleCommentCommandService;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDtoMapper;
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
@PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})") // 댓글 (등록/수정/삭제)는 인증된 'USER/ADMIN' 사용자만 요청 가능하다.
@Controller
public class CommentCommandController {

    private final ArticleCommentCommandService commentCommandService;
    private final ArticleCommentDtoMapper commentDtoMapper;

    @PostMapping("/article-comments")
    public String registerComment(ArticleCommentDto.RegisterReq req,
                                  @AuthenticationPrincipal PrincipalUserAccount userAccount) {

        commentCommandService.registerComment(userAccount.getUsername(), commentDtoMapper.toCommand(req));

        return "redirect:/articles/" + req.getArticleId();
    }

    @PutMapping("/article-comments/{id}")
    public String update(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalUserAccount userAccount,
                         ArticleCommentDto.UpdateReq req,
                         Long articleId) {

        commentCommandService.update(id, userAccount.getUsername(), commentDtoMapper.toCommand(req));

        return "redirect:/articles/" + articleId;
    }

    @DeleteMapping("/article-comments/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalUserAccount userAccount,
                         Long articleId) {

        commentCommandService.delete(id, userAccount.getUsername());

        return "redirect:/articles/" + articleId;
    }
}
