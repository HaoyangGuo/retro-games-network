package com.dhguo.retrogamesnetwork.email;

import com.dhguo.retrogamesnetwork.exception.VerificationEmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailService {

  private final JavaMailSender mailSender;
  private final SpringTemplateEngine templateEngine;
  
//  @Async
  public void sendEmail(
      String to,
      String username,
      String emailTemplate,
      String confirmationUrl,
      String activationCode,
      String subject)
      throws MessagingException, VerificationEmailSendException {
    String templateName;
    if (emailTemplate == null) {
      templateName = "confirm-email";
    } else {
      templateName = emailTemplate;
    }

    MimeMessage mimeMessage = mailSender.createMimeMessage();
    MimeMessageHelper helper =
        new MimeMessageHelper(
            mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED, StandardCharsets.UTF_8.name());
    Map<String, Object> properties = new HashMap<>();
    properties.put("username", username);
    properties.put("confirmation_url", confirmationUrl);
    properties.put("activation_code", activationCode);

    Context context = new Context();
    context.setVariables(properties);

    helper.setFrom("no-reply@dhguo.dev");
    helper.setTo(to);
    helper.setSubject(subject);

    String template = templateEngine.process(templateName, context);

    helper.setText(template, true);
    
    try {
      mailSender.send(mimeMessage);
    } catch (Exception exp) {
      exp.printStackTrace();
      throw new VerificationEmailSendException();
    }
  }
}
