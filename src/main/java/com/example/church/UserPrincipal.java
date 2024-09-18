package com.example.church;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.church.model.Ulogin;

import java.util.Collection;

public class UserPrincipal implements UserDetails {
    private Ulogin user;

    public UserPrincipal(Ulogin user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
       
        return null; 
    }

    @Override
    public String getPassword() {
        return user.getPassword(); 
    }

    @Override
    public String getUsername() {
        return user.getUsername();
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
