package com.example.projectboard.interfaces.dto.users;

import com.example.projectboard.domain.users.UserAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

public class UserAccountDto {

    @ToString
    @Getter
    @Builder
    public static class RegisterReq {
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$")
        private String username;

        @NotBlank @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}")
        private String password;

        @NotBlank
        private String name;

        @NotBlank @Email
        private String email;

        @NotBlank @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

        private UserAccount.RoleType role;
    }

    @ToString
    @Getter
    @Builder
    public static class UpdateReq {

        @NotBlank @Email
        private String email;

        @NotBlank @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

    }

    @ToString
    @Getter
    @Builder
    public static class RegisterForm {
        @NotBlank @Pattern(regexp = "^[a-zA-Z0-9]{4,12}$")
        private String username;

        @NotBlank @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,20}")
        private String password;

        @NotBlank
        private String name;

        @NotBlank @Email
        private String email;

        @NotBlank @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        private String phoneNumber;

        private UserAccount.RoleType role;
    }

    @ToString
    @Getter
    @Builder
    public static class UpdateForm {

        @NotNull
        private Long userId;

        private String username;

        private String name;

        @NotBlank
        private String beforeEmail;

        @NotBlank @Email
        private String email;

        @NotBlank @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$")
        private String phoneNumber;
    }

    @ToString
    @Getter
    @Builder
    public static class SearchCondition {
        private String username;
        private String email;
        private String phoneNumber;

        public static SearchCondition of(String username,
                                         String email,
                                         String phoneNumber) {
            return SearchCondition.builder()
                    .username(username)
                    .email(email)
                    .phoneNumber(phoneNumber)
                    .build();
        }
    }

    @ToString
    @Getter
    @Builder
    public static class MainInfoResponse {
        private Long userId;
        private String username;
        private String name;
        private String email;
        private String phoneNumber;
        private String role;
        private LocalDateTime createdAt;
        private LocalDateTime modifiedAt;
    }
}
