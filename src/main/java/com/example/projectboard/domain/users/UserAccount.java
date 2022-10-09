package com.example.projectboard.domain.users;

import com.example.projectboard.domain.JpaAuditingDateTimeFields;
import lombok.*;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.util.Objects;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_accounts", indexes = {
        @Index(columnList = "username", unique = true),
        @Index(columnList = "email", unique = true),
        @Index(columnList = "phoneNumber")
})
@Entity
public class UserAccount extends JpaAuditingDateTimeFields {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, updatable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(length = 100, unique = true, nullable = false)
    private String email;

    @Column(length = 50, nullable = false)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleType role;

    @Getter
    @RequiredArgsConstructor
    public enum RoleType {
        USER("ROLE_USER"),
        ADMIN("ROLE_ADMIN");

        private final String roleValue;
    }

    private UserAccount(String username,
                       String password,
                       String name,
                       String email,
                       String phoneNumber,
                       RoleType role) {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.role = role;
    }

    // 기본 일반 사용자 생성 메서드
    public static UserAccount of(String username,
                                 String password,
                                 String name,
                                 String email,
                                 String phoneNumber) {
        return new UserAccount(username, password, name, email, phoneNumber, RoleType.USER);
    }

    // 사용자 지정 ROLE_TYPE 의 사용자 생성 메서드 -> role == null 인 경우 : ROLE_USER 로 설정
    public static UserAccount of(String username,
                                 String password,
                                 String name,
                                 String email,
                                 String phoneNumber,
                                 RoleType role) {
        if(role == null) role = RoleType.USER;
        return new UserAccount(username, password, name, email, phoneNumber, role);
    }

    public void updatePassword(String encodedPwd) {
        this.password = encodedPwd;
    }

    public void updateUserInfo(String email, String phoneNumber) {
        if(StringUtils.hasText(email)) this.email = email;
        if(StringUtils.hasText(phoneNumber)) this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserAccount userAccount = (UserAccount) obj;
        return id != null && id.equals(userAccount.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
