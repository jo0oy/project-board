package com.example.projectboard.interfaces.api.v1.articles;

import com.example.projectboard.application.articles.ArticleQueryService;
import com.example.projectboard.common.response.ResultResponse;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ArticleQueryApiController {

    private final ArticleQueryService articleQueryService;
    private final ArticleDtoMapper articleDtoMapper;

    @GetMapping("/api/v1/articles/{id}")
    public ResponseEntity<?> getArticle(@PathVariable("id") Long id) {
        log.info("GET /api/v1/articles/{id}");
        var articleInfo = articleQueryService.getArticle(id);
        var data = articleDtoMapper.toDto(articleInfo);

        return ResponseEntity.ok()
                .body(ResultResponse.success("게시글 단건 조회 성공", data));
    }

    @GetMapping("/api/v1/articles")
    public ResponseEntity<?> articles(@RequestParam(name = "title", required = false) String title,
                                      @RequestParam(name = "hashtag", required = false) String hashtag,
                                      @RequestParam(name = "createdAt", required = false) String createdAt,
                                      @RequestParam(name = "createdBy", required = false) String createdBy,
                                      @PageableDefault(size = 15, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        log.info("GET /api/v1/articles");

        var condition= ArticleDto.SearchCondition.of(title, hashtag, createdAt, createdBy);

        var data
                = articleQueryService.articles(articleDtoMapper.toCommand(condition), pageable)
                .map(articleDtoMapper::toDto);

        return ResponseEntity.ok()
                .body(ResultResponse.success("전체 게시글 페이징 조회 성공", data));
    }

    @GetMapping("/api/v1/articles/list")
    public ResponseEntity<?> articles(@RequestParam(name = "title", required = false) String title,
                                      @RequestParam(name = "hashtag", required = false) String hashtag,
                                      @RequestParam(name = "createdAt", required = false) String createdAt,
                                      @RequestParam(name = "createdBy", required = false) String createdBy) {

        log.info("GET /api/v1/articles/list");

        var condition= ArticleDto.SearchCondition.of(title, hashtag, createdAt, createdBy);

        var data = articleQueryService.articleList(articleDtoMapper.toCommand(condition))
                .stream()
                .map(articleDtoMapper::toDto)
                .collect(Collectors.toList());

        return ResponseEntity.ok()
                .body(ResultResponse.success("전체 게시글 리스트 조회 성공", data));
    }
}
