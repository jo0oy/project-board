package com.example.projectboard.domain.users;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
@Builder
public class UserAccountInfo {

    private Long userId;
    private String username;
    private String name;
    private String email;
    private String phoneNumber;
    private String role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
