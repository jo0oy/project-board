package com.example.projectboard.domain.users;

import org.mapstruct.*;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserAccountInfoMapper {

    // ENTITY --> INFO/DTO
    @Mappings({
            @Mapping(target = "userId", source = "id"),
            @Mapping(target = "role", expression = "java(entity.getRole().getRoleValue())")
    })
    UserAccountInfo toInfo(UserAccount entity);

    UserAccountCacheDto toCacheDto(UserAccount entity);
}
