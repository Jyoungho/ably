package app.project.ably.core.security;

import app.project.ably.user.enums.UserRoleType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User auth 정보
 */
public class UserDetailsImpl implements UserDetails {

    /** 아이디 (UserDetails 규격) */
    @Getter
    @Setter
    private String username;

    /** 비밀번호 (UserDetails 규격) */
    @Getter
    @Setter
    private String password;

    @Setter
    private UserRoleType authorities;

    /** 권한 */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (this.authorities != null) {
            List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
            grantedAuthorities.add(new SimpleGrantedAuthority(authorities.name()));
            return grantedAuthorities;
        } else {
            return Collections.emptyList();
        }
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
