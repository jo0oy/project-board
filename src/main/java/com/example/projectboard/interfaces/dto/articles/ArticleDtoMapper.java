package com.example.projectboard.interfaces.dto.articles;

import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ArticleDtoMapper {

    // DTO -> COMMAND
    ArticleCommand.RegisterReq toCommand(ArticleDto.RegisterReq req);

    ArticleCommand.UpdateReq toCommand(ArticleDto.UpdateReq req);

    ArticleCommand.SearchCondition toCommand(ArticleDto.SearchCondition req);

    // INFO -> DTO
    ArticleDto.MainInfoResponse toDto(ArticleInfo.MainInfo info);

    ArticleDto.CommentInfoResponse toDto(ArticleCommentInfo.SimpleInfo info);

    ArticleDto.ArticleWithCommentsResponse toDto(ArticleInfo.ArticleWithCommentsInfo info);
}
