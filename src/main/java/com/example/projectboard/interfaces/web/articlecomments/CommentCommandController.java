package com.example.projectboard.interfaces.web.articlecomments;

import com.example.projectboard.application.articlecomments.ArticleCommentCommandService;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDtoMapper;
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
public class CommentCommandController {

    private final ArticleCommentCommandService commentCommandService;
    private final ArticleCommentDtoMapper commentDtoMapper;

    @PostMapping("/article-comments")
    public String registerComment(ArticleCommentDto.RegisterReq req) {
        commentCommandService.registerComment(commentDtoMapper.toCommand(req));

        return "redirect:/articles/" + req.getArticleId();
    }

    @PutMapping("/article-comments/{id}")
    public String update(@PathVariable Long id,
                         ArticleCommentDto.UpdateReq req, Long articleId) {
        commentCommandService.update(id, commentDtoMapper.toCommand(req));

        return "redirect:/articles/" + articleId;
    }

    @DeleteMapping("/article-comments/{id}")
    public String delete(@PathVariable Long id, Long articleId) {
        commentCommandService.delete(id);

        return "redirect:/articles/" + articleId;
    }
}
