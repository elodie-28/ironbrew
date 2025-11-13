package dev.elodie.ironbrew.security.user;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {
    private final UserService userService;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public RegistrationListener(
            UserService userService,
            JavaMailSender mailSender,
            TemplateEngine templateEngine
    ) {
        this.userService = userService;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        try {
            this.confirmRegistration(event);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) throws MessagingException {
        User user = event.getUser();
        String token = userService.createToken(user);

        String recipientAddress = user.getEmail();
        String subject = "Registration Confirmation";
        String appUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String verificationLink = appUrl + "/registrationConfirm?token=" + token;

        final Context ctx = new Context();
        ctx.setVariable("verificationLink", verificationLink);

        final String htmlContent = this.templateEngine.process("mail/email-registration-confirmation.html", ctx);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8"); // true = multipart
        message.setSubject(subject);
        message.setFrom("no-reply@ironbrew.com");
        message.setTo(recipientAddress);
        message.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }
}
