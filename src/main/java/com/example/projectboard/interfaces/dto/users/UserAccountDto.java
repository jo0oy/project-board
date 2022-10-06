package com.example.projectboard.interfaces.dto.users;

import com.example.projectboard.domain.users.UserAccount;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

public class UserAccountDto {

    @ToString
    @Getter
    @Builder
    public static class RegisterReq {
        private String username;
        private String password;
        private String name;
        private String email;
        private String phoneNumber;
        private UserAccount.RoleType role;
    }

    @ToString
    @Getter
    @Builder
    public static class UpdateReq {
        private String email;
        private String phoneNumber;
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
