package com.dhguo.retrogamesnetwork.auth;

import com.dhguo.retrogamesnetwork.email.EmailService;
import com.dhguo.retrogamesnetwork.exception.EmailAlreadyInUseException;
import com.dhguo.retrogamesnetwork.exception.VerificationEmailSendException;
import com.dhguo.retrogamesnetwork.role.Role;
import com.dhguo.retrogamesnetwork.role.RoleRepository;
import com.dhguo.retrogamesnetwork.security.JwtService;
import com.dhguo.retrogamesnetwork.user.Token;
import com.dhguo.retrogamesnetwork.user.TokenRepository;
import com.dhguo.retrogamesnetwork.user.User;
import com.dhguo.retrogamesnetwork.user.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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
  private final AuthenticationManager authenticationManager;
  private final JwtService jwtService;

  @Value("${application.security.mailing.frontend.activation-url}")
  private String activationUrl;

  @Transactional
  public void register(RegistrationRequest request) throws MessagingException {
    Role userRole =
        roleRepository
            .findByName("USER")
            .orElseThrow(() -> new IllegalStateException("ROLE USER was not initialized"));

    Optional<User> existedUser = userRepository.findByEmail(request.getEmail());
    if (existedUser.isPresent()) {
      throw new EmailAlreadyInUseException();
    }


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
    try {
      sendValidationEmail(user);
    } catch (VerificationEmailSendException exp) {

      throw new VerificationEmailSendException();
    }
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    var claims = new HashMap<String, Object>();
    var user = ((User) auth.getPrincipal());
    claims.put("fullName", user.fullName());
    var jwtToken = jwtService.generateToken(claims, user);
    return AuthenticationResponse.builder().token(jwtToken).build();
  }

  public void activateAccount(String token)
      throws MessagingException {
    Token savedToken =
        tokenRepository.findByToken(token).orElseThrow(() -> new RuntimeException("Invalid Token"));
    if (LocalDateTime.now().isAfter(savedToken.getExpiresAt())) {
      sendValidationEmail(savedToken.getUser());
      throw new RuntimeException(
          "Activation token has expired, A new token has been sent to the same email address");
    }
    var user =
        userRepository
            .findById(savedToken.getUser().getId())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));

    user.setEnabled(true);
    userRepository.save(user);
    savedToken.setValidatedAt(LocalDateTime.now());
    tokenRepository.save(savedToken);
  }

  private void sendValidationEmail(User user)
      throws MessagingException {
    var newToken = generateAndSaveActivationToken(user);

    emailService.sendEmail(
        user.getUsername(),
        user.fullName(),
        "activate_account",
        activationUrl,
        newToken,
        "Account Activation");
  }

  private String generateAndSaveActivationToken(User user) {
    // generate a token;
    String generatedToken = generateActivationCode(6);
    var token =
        Token.builder()
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
