package com.example.projectboard.domain.users;


import lombok.*;

import java.time.LocalDateTime;

@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class UserAccountCacheDto {

    private Long id;
    private String username;
    private String password;
    private String name;
    private String email;
    private String phoneNumber;
    private UserAccount.RoleType role;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
}
