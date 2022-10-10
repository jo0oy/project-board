package com.example.projectboard.interfaces.api.v1.articles;

import com.example.projectboard.application.articles.ArticleCommandService;
import com.example.projectboard.common.response.ResultResponse;
import com.example.projectboard.interfaces.dto.articles.ArticleDto;
import com.example.projectboard.interfaces.dto.articles.ArticleDtoMapper;
import com.example.projectboard.security.PrincipalUserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ArticleCommandApiController {

    private final ArticleCommandService articleCommandService;
    private final ArticleDtoMapper articleDtoMapper;

    @PostMapping("/api/v1/articles")
    public ResponseEntity<?> registerArticle(@RequestBody ArticleDto.RegisterReq req,
                                             @AuthenticationPrincipal PrincipalUserAccount userAccount) {

        var registeredArticle
                = articleCommandService.registerArticle(userAccount.getUsername(), articleDtoMapper.toCommand(req));

        var data = articleDtoMapper.toDto(registeredArticle);

        return ResponseEntity.created(URI.create("/api/v1/articles"))
                .body(ResultResponse.success("게시글 등록 성공", data));
    }

    @PutMapping("/api/v1/articles/{id}")
    public ResponseEntity<?> updateArticle(@PathVariable("id") Long id,
                                           @RequestBody ArticleDto.UpdateReq req,
                                           @AuthenticationPrincipal PrincipalUserAccount userAccount) {

        articleCommandService.update(id, userAccount.getUsername(), articleDtoMapper.toCommand(req));

        return ResponseEntity.ok()
                .body(ResultResponse.success("게시글 수정 성공"));
    }
}
