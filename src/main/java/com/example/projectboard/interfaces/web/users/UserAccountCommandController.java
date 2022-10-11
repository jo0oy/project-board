package com.example.projectboard.interfaces.web.users;

import com.example.projectboard.application.users.UserAccountCommandService;
import com.example.projectboard.interfaces.dto.users.UserAccountDto;
import com.example.projectboard.interfaces.dto.users.UserAccountDtoMapper;
import com.example.projectboard.security.PrincipalUserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserAccountCommandController {

    private final UserAccountCommandService userAccountCommandService;
    private final UserAccountDtoMapper userAccountDtoMapper;

    @PostMapping("/user-accounts")
    public String registerUser(UserAccountDto.RegisterReq req) {

        userAccountCommandService.registerUser(userAccountDtoMapper.toCommand(req));

        return "redirect:/accounts/sign-up/success";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/user-accounts/{id}")
    public String updateUserInfo(UserAccountDto.UpdateReq req,
                                 @PathVariable Long id,
                                 @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        userAccountCommandService.updateUserInfo(id, principalUserAccount.getUsername(), userAccountDtoMapper.toCommand(req));

        return "redirect:/accounts/me";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @DeleteMapping("/user-accounts/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        userAccountCommandService.delete(id, principalUserAccount.getUsername());

        return "redirect:/";
    }
}
