package com.itkedu.service;

import com.itkedu.model.User;
import com.itkedu.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.userinfo.*;
import org.springframework.security.oauth2.core.user.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class SocialAppService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    public SocialAppService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger =
            LoggerFactory.getLogger(SocialAppService.class);

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
                new DefaultOAuth2UserService();

        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        Map<String,Object> attributes = oAuth2User.getAttributes();

        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {

                	User newUser = new User(null, name, email, "ROLE_USER", "GOOGLE");
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole("ROLE_USER");
                    newUser.setProvider("GOOGLE");

                    logger.info("Saving new OAuth user: {}", email);

                    return userRepository.save(newUser);
                });

        return new DefaultOAuth2User(
                java.util.List.of(() -> user.getRole()),
                attributes,
                "email"
        );
    }
}