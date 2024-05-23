package com.dhguo.book.auth;

import com.dhguo.book.email.EmailService;
import com.dhguo.book.email.EmailTemplateName;
import com.dhguo.book.role.Role;
import com.dhguo.book.role.RoleRepository;
import com.dhguo.book.user.Token;
import com.dhguo.book.user.TokenRepository;
import com.dhguo.book.user.User;
import com.dhguo.book.user.UserRepository;
import jakarta.mail.MessagingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

  private final RoleRepository roleRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenRepository tokenRepository;
  private final EmailService emailService;

  @Value("${application.security.mailing.frontend.activation-url}")
  private String activationUrl;

  public void register(RegistrationRequest request) throws MessagingException {
    Role userRole =
        roleRepository
            .findByName("USER")
            .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));

    User user =
        User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .accountLocked(false)
            .enabled(false)
            .roles(List.of(userRole))
            .build();
    userRepository.save(user);
    sendValidationEmail(user);
  }

  private void sendValidationEmail(User user) throws MessagingException {
    var newToken = generateAndSaveActivationToken(user);


    emailService.sendEmail(
        user.getUsername(),
        user.fullName(),
        EmailTemplateName.ACTIVATE_ACCOUNT,
        activationUrl,
        newToken,
        "Account Activation"
    );
  }

  private String generateAndSaveActivationToken(User user) {
    // generate a token;
    String generatedToken = generateActivationCode(6);
    var token = Token.builder()
        .token(generatedToken)
        .user(user)
        .createdAt(LocalDateTime.now())
        .expiresAt(LocalDateTime.now().plusMinutes(15))
        .build();
    tokenRepository.save(token);
    return generatedToken;
  }

  private String generateActivationCode(int length) {
    String characters = "0123456789";
    StringBuilder codeBuilder = new StringBuilder();
    SecureRandom secureRandom = new SecureRandom();
    for (int i = 0; i < length; i++) {
      int randomIndex = secureRandom.nextInt(characters.length());
      codeBuilder.append(characters.charAt(randomIndex));
    }
    return codeBuilder.toString();
  }
}
