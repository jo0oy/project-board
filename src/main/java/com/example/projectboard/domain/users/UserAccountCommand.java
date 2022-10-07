package com.example.projectboard.domain.users;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

public class UserAccountCommand {

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

        public UserAccount toEntity() {
            return UserAccount.of(username, password, name, email, phoneNumber, role);
        }
    }

    @ToString
    @Getter
    @Builder
    public static class UpdateReq {
        private String email;
        private String phoneNumber;
    }
}
