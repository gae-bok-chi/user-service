package com.gaebokchi.userservice.service;

import com.gaebokchi.userservice.entity.User;
import com.gaebokchi.userservice.exception.NotFoundUserException;
import com.gaebokchi.userservice.repository.UserRepository;
import com.gaebokchi.userservice.vo.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User saveUser(OAuth2User oAuth2User) {
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = String.valueOf(attributes.get("email"));
        String name = String.valueOf(attributes.get("name"));
        String picture = String.valueOf(attributes.get("picture"));
        String password = String.valueOf(attributes.getOrDefault("password", "not exist"));

        User user = userRepository.findByEmail(email)
                .map(u -> u.updateModifiedDate(name, picture))
                .orElse(User.builder()
                        .name(name)
                        .password(password)
                        .email(email)
                        .picture(picture)
                        .role(Role.USER)
                        .build());
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        Optional<User> findUser = userRepository.findByEmail(email);

        if (findUser.isEmpty()) {
            throw new NotFoundUserException(email + " 유저를 찾을 수 없습니다.");
        }

        return findUser.get();
    }
}
