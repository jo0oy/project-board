package com.example.projectboard.interfaces.web.users;

import com.example.projectboard.application.users.UserAccountQueryService;
import com.example.projectboard.interfaces.dto.users.UserAccountDto;
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
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/accounts")
@Controller
public class UserAccountViewController {

    private final UserAccountQueryService userAccountQueryService;
    private final UserAccountDtoMapper userAccountDtoMapper;

    // 회원가입 페이지
    @GetMapping("/sign-up")
    public String signUpPage(Model model) {
        var registerForm = UserAccountDto.RegisterForm.builder().build();
        model.addAttribute("registerForm", registerForm);
        return "users/sign-up";
    }

    // 회원가입 성공 페이지
    @GetMapping("/sign-up/success")
    public String signUpSuccessPage() {
        return "users/sign-up-success";
    }

    // myAccount 페이지
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/me")
    public String myAccountPage(@AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                Model model) {

        var userInfo = userAccountQueryService.getUserAccountInfo(principalUserAccount.getUsername());
        model.addAttribute("userInfo", userAccountDtoMapper.toDto(userInfo));

        return "users/user-info";
    }

    // {username}의 계정 정보 페이지 -> 본인 혹은 관리자 계정만 접근 가능
    @PreAuthorize("(hasRole('ROLE_USER') and (#username == authentication.principal.username)) or hasRole('ROLE_ADMIN')")
    @GetMapping("/{username}")
    public String userInfoPage(@PathVariable String username,
                               @AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                               Model model) {

        var userInfo = userAccountQueryService.getUserAccountInfo(username, principalUserAccount.getUsername());
        model.addAttribute("userInfo", userInfo);

        return "users/user-info";
    }

    // myAccount 정보 수정 페이지
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/me/edit")
    public String myAccountEditPage(@AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                    Model model) {

        var userInfo = userAccountQueryService.getUserAccountInfo(principalUserAccount.getUsername());
        var updateForm = userAccountDtoMapper.toFormDto(userInfo);

        model.addAttribute("updateForm", updateForm);

        return "users/edit-form";
    }

    // {username}의 계정 정보 수정 페이지 -> 본인 혹은 관리자 계정만 접근 가능
    @PreAuthorize("(hasRole('ROLE_USER') and (#username == authentication.principal.username)) or hasRole('ROLE_ADMIN')")
    @GetMapping("/{username}/edit")
    public String userInfoEditPage(@PathVariable String username,
                                   @AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                   Model model) {

        var userInfo = userAccountQueryService.getUserAccountInfo(username, principalUserAccount.getUsername());
        var updateForm = userAccountDtoMapper.toFormDto(userInfo);

        model.addAttribute("updateForm", updateForm);

        return "users/edit-form";
    }
}
