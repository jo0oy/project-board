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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RequiredArgsConstructor
@Controller
public class UserAccountCommandController {

    private final UserAccountCommandService userAccountCommandService;
    private final UserAccountDtoMapper userAccountDtoMapper;

    @PostMapping("/accounts/sign-up")
    public String registerUser(@Valid @ModelAttribute("registerForm") UserAccountDto.RegisterForm registerForm,
                               BindingResult bindingResult) {

        // 중복 아이디 검증
        if (!userAccountCommandService.verifyDuplicateUsername(registerForm.getUsername())) {
            log.debug("중복된 아이디입니다! username={}", registerForm.getUsername());
            bindingResult.rejectValue("username", "Duplicate");
        }

        // 중복 이메일 검증
        if (!userAccountCommandService.verifyDuplicateEmail(registerForm.getEmail())) {
            log.debug("이미 사용중인 이메일입니다! email={}", registerForm.getEmail());
            bindingResult.rejectValue("email", "Duplicate");
        }

        if (bindingResult.hasErrors()) {
            log.debug("errors={}", bindingResult);
            return "users/sign-up";
        }

        userAccountCommandService.registerUser(userAccountDtoMapper.toCommand(registerForm));

        return "redirect:/accounts/sign-up/success";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @PutMapping("/accounts/{id}/edit")
    public String updateUserInfo(@PathVariable Long id,
                                 @AuthenticationPrincipal PrincipalUserAccount principalUserAccount,
                                 @Valid @ModelAttribute("updateForm") UserAccountDto.UpdateForm updateForm,
                                 BindingResult bindingResult) {

        // 이메일에 수정된 내용이 있는 경우 중복 이메일 검증 로직 실행
        if (!updateForm.getBeforeEmail().equals(updateForm.getEmail())
                && !userAccountCommandService.verifyDuplicateEmail(updateForm.getEmail())) {
            log.debug("이미 사용중인 이메일입니다! email={}", updateForm.getEmail());
            bindingResult.rejectValue("email", "Duplicate");
        }

        if (bindingResult.hasErrors()) {
            log.debug("errors={}", bindingResult);
            return "users/edit-form";
        }

        userAccountCommandService.updateUserInfo(id, principalUserAccount.getUsername(), userAccountDtoMapper.toCommand(updateForm));

        return "redirect:/accounts/me";
    }

    @PreAuthorize("hasAnyRole({'ROLE_USER', 'ROLE_ADMIN'})")
    @DeleteMapping("/accounts/{id}")
    public String delete(@PathVariable Long id,
                         @AuthenticationPrincipal PrincipalUserAccount principalUserAccount) {

        userAccountCommandService.delete(id, principalUserAccount.getUsername());

        return "redirect:/";
    }
}
