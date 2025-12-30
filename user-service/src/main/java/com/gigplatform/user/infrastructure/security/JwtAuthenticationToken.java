package com.gigplatform.user.infrastructure.security;

import com.gigplatform.user.domain.model.UserType;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class JwtAuthenticationToken extends AbstractAuthenticationToken {

    private final UUID userId;
    private final String email;
    private final UserType userType;

    public JwtAuthenticationToken(UUID userId, String email, UserType userType) {
        super(getAuthorities(userType));
        this.userId = userId;
        this.email = email;
        this.userType = userType;
        setAuthenticated(true);
    }

    private static Collection<? extends GrantedAuthority> getAuthorities(UserType userType) {
        return List.of(new SimpleGrantedAuthority("ROLE_" + userType.name()));
    }

    @Override
    public Object getCredentials() {
        return null; // JWT tokens don't expose credentials
    }

    @Override
    public Object getPrincipal() {
        return this.userId;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }
}
