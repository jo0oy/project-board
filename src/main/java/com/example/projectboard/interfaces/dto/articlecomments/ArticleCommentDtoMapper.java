package com.example.projectboard.interfaces.dto.articlecomments;

import com.example.projectboard.domain.articlecomments.ArticleCommentCommand;
import com.example.projectboard.domain.articlecomments.ArticleCommentInfo;
import org.mapstruct.*;

import java.util.List;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface ArticleCommentDtoMapper {

    // DTO -> COMMAND
    ArticleCommentCommand.RegisterReq toCommand(ArticleCommentDto.RegisterReq req);

    ArticleCommentCommand.UpdateReq toCommand(ArticleCommentDto.UpdateReq req);

    @Mapping(target = "articleId", source = "parentArticleId")
    ArticleCommentCommand.RegisterReq toCommand(ArticleCommentDto.RegisterForm form);

    @Mappings({
            @Mapping(target = "articleId", source = "articleId"),
            @Mapping(target = "commentBody", source = "childCommentBody"),
            @Mapping(target = "parentId", source = "parentCommentId")
    })
    ArticleCommentCommand.RegisterReq toCommand(ArticleCommentDto.RegisterChildForm form);

    @Mapping(target = "commentBody", source = "updateCommentBody")
    ArticleCommentCommand.UpdateReq toCommand(ArticleCommentDto.UpdateForm form);

    ArticleCommentCommand.SearchCondition toCommand(ArticleCommentDto.SearchCondition req);

    // INFO -> DTO
    ArticleCommentDto.MainInfoResponse toDto(ArticleCommentInfo.MainInfo info);

    ArticleCommentDto.SimpleInfoResponse toDto(ArticleCommentInfo.SimpleInfo info);

    ArticleCommentDto.WithChildCommentsInfoResponse toDto(ArticleCommentInfo.WithChildCommentsInfo info);

    ArticleCommentDto.GroupByArticleInfoResponse toDto(Long articleId, List<ArticleCommentInfo.MainInfo> comments);
}
