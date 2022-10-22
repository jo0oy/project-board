package com.example.projectboard.interfaces.dto.hashtags;

import com.example.projectboard.domain.hashtags.HashtagInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface HashtagDtoMapper {

    // INFO -> DTO

    HashtagDto.MainInfoResponse toDto(HashtagInfo info);
}
