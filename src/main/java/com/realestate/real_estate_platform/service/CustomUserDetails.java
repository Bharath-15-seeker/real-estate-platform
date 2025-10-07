package com.realestate.real_estate_platform.service;

import com.realestate.real_estate_platform.entity.User; // Assuming your User entity is here
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

/**
 * Custom implementation of UserDetails to expose the database ID (Long)
 * which is required for adding the ID to the JWT payload.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    /**
     * Exposes the primary key ID of the User entity.
     * This is the ID we will add to the JWT.
     */
    public Long getId() {
        // ASSUMPTION: The com.realestate.real_estate_platform.entity.User class has a getId() method
        return user.getId();
    }

    // Delegation methods for standard UserDetails functionality

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Replicates the authority logic from UserDetailsServiceImpl
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail(); // email is used as the username
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
