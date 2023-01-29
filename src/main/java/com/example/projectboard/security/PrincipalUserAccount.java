package com.example.projectboard.security;

import com.example.projectboard.domain.users.UserAccount;
import com.example.projectboard.domain.users.UserAccountCacheDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Set;

public class PrincipalUserAccount implements UserDetails {

    private Long id;
    private String username;
    private String password;
    private UserAccount.RoleType role;

    public PrincipalUserAccount(UserAccount entity) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.password = entity.getPassword();
        this.role = entity.getRole();
    }

    public PrincipalUserAccount(UserAccountCacheDto cacheDto) {
        this.id = cacheDto.getId();
        this.username = cacheDto.getUsername();
        this.password = cacheDto.getPassword();
        this.role = cacheDto.getRole();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Set.of(new SimpleGrantedAuthority(this.role.getRoleValue()));
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
