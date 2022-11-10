package com.example.projectboard.interfaces.dto.articles;

import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import com.example.projectboard.domain.articles.ArticleCommand;
import com.example.projectboard.domain.articles.ArticleInfo;
import com.example.projectboard.domain.likes.ArticleLikeCommand;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ArticleDtoMapper {

    // DTO -> COMMAND
    @Mapping(target = "hashtagNames", source = "hashtagNames")
    ArticleCommand.RegisterReq toCommand(ArticleDto.RegisterReq req, List<String> hashtagNames);

    @Mapping(target = "hashtagNames", source = "hashtagNames")
    ArticleCommand.UpdateReq toCommand(ArticleDto.UpdateReq req, List<String> hashtagNames);

    @Mapping(target = "hashtagNames", source = "hashtagNames")
    ArticleCommand.RegisterReq toCommand(ArticleDto.RegisterForm form, List<String> hashtagNames);

    @Mapping(target = "hashtagNames", source = "hashtagNames")
    ArticleCommand.UpdateReq toCommand(ArticleDto.UpdateForm form, List<String> hashtagNames);

    ArticleCommand.SearchCondition toCommand(ArticleDto.SearchCondition req);

    ArticleLikeCommand.SearchCondition toArticleLikeCommand(ArticleDto.SearchCondition req);

    // INFO -> DTO
    ArticleDto.MainInfoResponse toDto(ArticleInfo.MainInfo info);

    ArticleDto.HashtagInfoResponse toDto(ArticleInfo.HashtagInfo info);

    ArticleDto.CommentInfoResponse toDto(ArticleCommentInfo.SimpleInfo info);

    ArticleDto.ArticleWithCommentsResponse toDto(ArticleInfo.ArticleWithCommentsInfo info);

    @Mapping(target = "hashtagContent", expression = "java(info.getHashtagStringContent())")
    ArticleDto.UpdateForm toFormDto(ArticleInfo.MainInfo info);
}
