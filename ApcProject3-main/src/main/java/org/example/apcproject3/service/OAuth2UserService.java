package org.example.apcproject3.service;

import org.example.apcproject3.entity.AuthProvider;
import org.example.apcproject3.entity.User;
import org.example.apcproject3.entity.UserRole;
import org.example.apcproject3.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        return processOAuth2User(userRequest, oauth2User);
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        Map<String, Object> attributes = oauth2User.getAttributes();
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        String email = getEmailFromOAuth2User(oauth2User, registrationId);
        String name = getNameFromOAuth2User(oauth2User, registrationId);
        String providerId = getProviderIdFromOAuth2User(oauth2User, registrationId);

        Optional<User> userOptional = userRepository.findByEmail(email);
        User user;

        if (userOptional.isPresent()) {
            user = userOptional.get();
            // Update existing user with OAuth2 info if needed
            if (!user.getProvider().equals(getAuthProvider(registrationId))) {
                user.setProvider(getAuthProvider(registrationId));
                user.setProviderId(providerId);
                userRepository.save(user);
            }
        } else {
            // Create new user
            user = createNewOAuth2User(email, name, registrationId, providerId);
        }

        return user;
    }

    private User createNewOAuth2User(String email, String name, String registrationId, String providerId) {
        User user = new User();
        user.setEmail(email);
        user.setUsername(email); // Use email as username for OAuth2 users
        user.setFirstName(getFirstName(name));
        user.setLastName(getLastName(name));
        user.setProvider(getAuthProvider(registrationId));
        user.setProviderId(providerId);
        user.setRole(UserRole.CUSTOMER);
        user.setEnabled(true);
        user.setPassword(""); // OAuth2 users don't need password

        return userRepository.save(user);
    }

    private String getEmailFromOAuth2User(OAuth2User oauth2User, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) oauth2User.getAttributes().get("email");
        } else if ("facebook".equals(registrationId)) {
            return (String) oauth2User.getAttributes().get("email");
        }
        throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
    }

    private String getNameFromOAuth2User(OAuth2User oauth2User, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) oauth2User.getAttributes().get("name");
        } else if ("facebook".equals(registrationId)) {
            return (String) oauth2User.getAttributes().get("name");
        }
        return "Unknown User";
    }

    private String getProviderIdFromOAuth2User(OAuth2User oauth2User, String registrationId) {
        if ("google".equals(registrationId)) {
            return (String) oauth2User.getAttributes().get("sub");
        } else if ("facebook".equals(registrationId)) {
            return (String) oauth2User.getAttributes().get("id");
        }
        return null;
    }

    private AuthProvider getAuthProvider(String registrationId) {
        if ("google".equals(registrationId)) {
            return AuthProvider.GOOGLE;
        } else if ("facebook".equals(registrationId)) {
            return AuthProvider.FACEBOOK;
        }
        return AuthProvider.LOCAL;
    }

    private String getFirstName(String fullName) {
        if (fullName != null && fullName.contains(" ")) {
            return fullName.split(" ")[0];
        }
        return fullName != null ? fullName : "Unknown";
    }

    private String getLastName(String fullName) {
        if (fullName != null && fullName.contains(" ")) {
            String[] parts = fullName.split(" ");
            return parts[parts.length - 1];
        }
        return "";
    }
}
