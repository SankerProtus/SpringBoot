package com.codeQuest.Chatterly.Security.Oauth2;

import com.codeQuest.Chatterly.Entities.User;
import com.codeQuest.Chatterly.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder PasswordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);

        // Registering User
        String email = oauthUser.getAttribute("email");
        userRepository.findByEmail(email)
                .orElseGet(() -> registerNewUser(oauthUser));

        return oauthUser;
    }

    private User registerNewUser(OAuth2User oauthUser) {
        User newUser = new User();
        newUser.setEmail(oauthUser.getAttribute("email"));
        newUser.setUsername(oauthUser.getAttribute("name"));
        newUser.setProfilePicture(oauthUser.getAttribute("picture"));
        String randomPassword = PasswordEncoder.encode(UUID.randomUUID().toString());
        newUser.setPasswordHash(PasswordEncoder.encode(randomPassword));
        return userRepository.save(newUser);
    }
}
