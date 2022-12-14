package com.example.projectboard.domain.users;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@Builder
public class UserAccountSearchCondition {
    private String username;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;
}
