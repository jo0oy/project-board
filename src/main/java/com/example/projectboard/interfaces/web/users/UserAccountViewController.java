package com.example.projectboard.interfaces.web.users;

import com.example.projectboard.application.users.UserAccountQueryService;
import com.example.projectboard.interfaces.dto.users.UserAccountDtoMapper;
import com.example.projectboard.security.PrincipalUserAccount;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserAccountViewController {

    private final UserAccountQueryService userAccountQueryService;
    private final UserAccountDtoMapper userAccountDtoMapper;

    // 회원가입 페이지
    @GetMapping("/accounts/sign-up")
    public String signUpPage() {
        return "users/sign-up";
    }

    // 회원가입 성공 페이지
    @GetMapping("/accounts/sign-up/success")
    public String signUpSuccessPage() {
        return "users/sign-up-success";
    }

    // myAccount 페이지
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/accounts/me")
    public String myAccountPage(Model model,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        var userInfo = userAccountQueryService.getUserAccountInfo(principalUserAccount.getUsername());
        model.addAttribute("userInfo", userAccountDtoMapper.toDto(userInfo));

        return "users/user-info";
    }

    // {username}의 계정 정보 페이지 -> 본인 혹은 관리자 계정만 접근 가능
    @PreAuthorize("(hasRole('ROLE_USER') and (#username == authentication.principal.username)) or hasRole('ROLE_ADMIN')")
    @GetMapping("/accounts/{username}")
    public String userInfoPage(Model model,
                                @PathVariable String username,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        var userInfo = userAccountQueryService.getUserAccountInfo(username, principalUserAccount.getUsername());
        model.addAttribute("userInfo", userAccountDtoMapper.toDto(userInfo));

        return "users/user-info";
    }

    // myAccount 정보 수정 페이지
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/accounts/me/edit")
    public String myAccountEditPage(Model model,
                                @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        var userInfo = userAccountQueryService.getUserAccountInfo(principalUserAccount.getUsername());
        model.addAttribute("userInfo", userAccountDtoMapper.toDto(userInfo));

        return "users/edit-form";
    }

    // {username}의 계정 정보 수정 페이지 -> 본인 혹은 관리자 계정만 접근 가능
    @PreAuthorize("(hasRole('ROLE_USER') and (#username == authentication.principal.username)) or hasRole('ROLE_ADMIN')")
    @GetMapping("/accounts/{username}/edit")
    public String userInfoEditPage(Model model,
                                    @PathVariable String username,
                                    @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        var userInfo = userAccountQueryService.getUserAccountInfo(username, principalUserAccount.getUsername());
        model.addAttribute("userInfo", userAccountDtoMapper.toDto(userInfo));

        return "users/edit-form";
    }
}
