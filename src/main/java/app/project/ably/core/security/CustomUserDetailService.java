package app.project.ably.core.security;

import app.project.ably.core.handler.exception.BizException;
import app.project.ably.user.entity.User;
import app.project.ably.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByLoginId(username)
                .orElseThrow(() -> BizException
                        .withUserMessageKey("exception.user.not.found")
                        .build());

        UserDetailsImpl userDetailsImpl = new UserDetailsImpl();
        userDetailsImpl.setUsername(user.getLoginId());
        userDetailsImpl.setPassword(user.getPassword());
        userDetailsImpl.setAuthorities(user.getUserRoleType());
        return userDetailsImpl;
    }
}
