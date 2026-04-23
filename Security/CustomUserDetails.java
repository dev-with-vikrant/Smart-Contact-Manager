package com.smart.smartcontactmanager.Security;

import com.smart.smartcontactmanager.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private User users;

    public CustomUserDetails(User user) {
        this.users = user;
    }
    // Implement all methods from UserDetails interface...
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(users.getRole()));
    }
    @Override
    public String getPassword() { return users.getPassword(); }
    @Override
    public String getUsername() { return users.getEmail(); }
    // ... isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled
}