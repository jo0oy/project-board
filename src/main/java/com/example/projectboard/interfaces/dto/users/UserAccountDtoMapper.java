package com.example.projectboard.interfaces.dto.users;

import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserAccountDtoMapper {

    // DTO -> COMMAND

    UserAccountCommand.RegisterReq toCommand(UserAccountDto.RegisterReq req);

    UserAccountCommand.RegisterReq toCommand(UserAccountDto.RegisterForm form);

    UserAccountCommand.UpdateReq toCommand(UserAccountDto.UpdateReq req);

    UserAccountCommand.UpdateReq toCommand(UserAccountDto.UpdateForm form);

    UserAccountCommand.SearchCondition toCommand(UserAccountDto.SearchCondition req);


    // INFO -> DTO

    UserAccountDto.MainInfoResponse toDto(UserAccountInfo info);

    @Mapping(target = "beforeEmail", source = "email")
    UserAccountDto.UpdateForm toFormDto(UserAccountInfo info);
}
