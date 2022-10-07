package com.example.projectboard.interfaces.dto.users;

import com.example.projectboard.domain.users.UserAccountCommand;
import com.example.projectboard.domain.users.UserAccountInfo;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = "spring",
        injectionStrategy = InjectionStrategy.CONSTRUCTOR,
        unmappedTargetPolicy = ReportingPolicy.ERROR
)
public interface UserAccountDtoMapper {

    // DTO -> COMMAND

    UserAccountCommand.RegisterReq toCommand(UserAccountDto.RegisterReq req);

    UserAccountCommand.UpdateReq toCommand(UserAccountDto.UpdateReq req);

    // INFO -> DTO

    UserAccountDto.MainInfoResponse toDto(UserAccountInfo info);
}
