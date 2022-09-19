package com.example.projectboard.interfaces.api.v1.articlecomments;

import com.example.projectboard.application.articlecomments.ArticleCommentQueryService;
import com.example.projectboard.common.response.ResultResponse;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDto;
import com.example.projectboard.interfaces.dto.articlecomments.ArticleCommentDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@RestController
public class CommentQueryApiController {
    private final ArticleCommentQueryService commentQueryService;
    private final ArticleCommentDtoMapper commentDtoMapper;

    @GetMapping("/article-comments/{id}")
    public ResponseEntity<?> getComment(@PathVariable("id") Long id) {
        var registeredComment = commentQueryService.getComment(id);
        var data = commentDtoMapper.toDto(registeredComment);

        return ResponseEntity.ok()
                .body(ResultResponse.success("게시물 댓글 단건 조회 성공", data));
    }

    @GetMapping("/article-comments")
    public ResponseEntity<?> comments(@RequestParam(name = "createdAt", required = false) String createdAt,
                                      @RequestParam(name = "createdBy", required = false) String createdBy,
                                      @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        var condition= ArticleCommentDto.SearchCondition.of(createdAt, createdBy);

        var data
                = commentQueryService.comments(commentDtoMapper.toCommand(condition), pageable)
                .map(commentDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.success("검색조건에 따른 댓글 리스트 페이징 조회 성공", data));
    }

    @GetMapping("/article-comments/list")
    public ResponseEntity<?> commentsList(@RequestParam(name = "createdAt", required = false) String createdAt,
                                          @RequestParam(name = "createdBy", required = false) String createdBy) {

        var condition= ArticleCommentDto.SearchCondition.of(createdAt, createdBy);

        var data
                = commentQueryService.comments(commentDtoMapper.toCommand(condition))
                .stream()
                .map(commentDtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(ResultResponse.success("검색조건에 따른 댓글 리스트 조회 성공", data));
    }

    @GetMapping("/articles/{articleId}/comments")
    public ResponseEntity<?> commentsByArticleId(@PathVariable("articleId") Long articleId,
                                                 @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        var data
                = commentQueryService.commentsByArticleId(articleId, pageable).map(commentDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.success("게시글 ID=" + articleId + "의 댓글 리스트 페이징 조회 성공", data));
    }

    @GetMapping("/articles/comments")
    public ResponseEntity<?> commentsGroupByArticleId() {
        var listMap = commentQueryService.commentsGroupByArticleId();

        var data = new ArrayList<>();

        for (Map.Entry<Long, List<ArticleCommentInfo.MainInfo>> entry : listMap.entrySet()) {
            data.add(commentDtoMapper.toDto(entry.getKey(), entry.getValue()));
        }

        return ResponseEntity.ok()
                .body(ResultResponse.success("게시물별 댓글 리스트 조회 성공", data));
    }
}
