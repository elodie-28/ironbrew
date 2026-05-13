package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.exception.*;
import dev.elodie.ironbrew.utils.AppUrlProvider;
import dev.elodie.ironbrew.utils.TokenGenerator;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.Duration;
import java.time.OffsetDateTime;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final AppUrlProvider appUrlProvider;
    private final TokenGenerator tokenGenerator;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            UserMapper userMapper,
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            AppUrlProvider appUrlProvider,
            TokenGenerator tokenGenerator
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.appUrlProvider = appUrlProvider;
        this.tokenGenerator = tokenGenerator;
    }

    public User register(RegisterRequest registerRequest) {
        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try{
            return userRepository.save(user);
        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains(User.UNIQUE_EMAIL_CONSTRAINT)){
                throw new EmailAlreadyTakenException();
            } else if (e.getMessage().contains(User.UNIQUE_NAME_CONSTRAINT)) {
                throw new NameAlreadyTakenException();
            }
        }
        return null;
    }

    public String createConfirmationToken(User user) {
        String token = tokenGenerator.generateToken();

        user.setConfirmationToken(token);
        user.setConfirmationSentAt(OffsetDateTime.now());
        userRepository.save(user);
        return token;
    }

    public void confirmToken(String token) {
        if(token == null || token.isEmpty()){
            throw new InvalidTokenException();
        }

        User user = userRepository.findByConfirmationToken(token).orElseThrow(InvalidTokenException::new);

        //if the token has been sent more than 24h before now, then the token is invalid
        if(Math.abs(Duration.between(user.getConfirmationSentAt(), OffsetDateTime.now()).toHours()) > 24){
            throw new InvalidTokenException();
        }

        user.setConfirmedAt(OffsetDateTime.now());

        user.setConfirmationToken(null);
        user.setConfirmationSentAt(null);

        userRepository.save(user);
    }

    public void createPasswordResetToken(ForgotPasswordRequest forgotPasswordRequest) throws MessagingException {
        User user = userRepository.findByEmail(forgotPasswordRequest.getEmail()).orElseThrow(UserNotFoundException::new);

        String token = tokenGenerator.generateToken();

        user.setPasswordResetToken(token);
        user.setPasswordResetSentAt(OffsetDateTime.now());
        userRepository.save(user);

        sendForgotPasswordEmail(user);
    }

    private void sendForgotPasswordEmail(User user) throws MessagingException {
        String recipientAddress = user.getEmail();
        String subject = "Reset Your Password";
        String appUrl = appUrlProvider.getAppUrl();
        String passwordResetLink = appUrl + "/password-reset?token=" + user.getPasswordResetToken();

        final Context ctx = new Context();
        ctx.setVariable("passwordResetLink", passwordResetLink);

        final String htmlContent = this.templateEngine.process("mail/email-password-reset.html", ctx);

        final MimeMessage mimeMessage = this.mailSender.createMimeMessage();
        final MimeMessageHelper message =
                new MimeMessageHelper(mimeMessage, true, "UTF-8");
        message.setSubject(subject);
        message.setFrom("no-reply@ironbrew.com");
        message.setTo(recipientAddress);
        message.setText(htmlContent, true);
        mailSender.send(mimeMessage);
    }

    public void changePassword(PasswordResetRequest passwordResetRequest) {
        User user = userRepository.findByPasswordResetToken(passwordResetRequest.getToken()).orElseThrow(UserNotFoundException::new);

        //if the token has been sent more than 15min before now, then the token is invalid
        if(Math.abs(Duration.between(user.getPasswordResetSentAt(), OffsetDateTime.now()).toMinutes()) > 15){
            throw new InvalidTokenException();
        }

        user.setPassword(passwordEncoder.encode(passwordResetRequest.getPassword()));

        //delete the password reset token from the db so it's not reusable
        user.setPasswordResetToken(null);
        user.setPasswordResetSentAt(null);

        userRepository.save(user);
    }

    public AccountDto findAccountDtoByEmail(String email) {
        return userRepository.findAccountDtoByEmail(email).orElseThrow(UserNotFoundException::new);
    }

    public void updateFromAccountDto(AccountDto accountDto, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        userMapper.updateFromAccountDto(accountDto, user);
        try{
            userRepository.save(user);
        }catch (DataIntegrityViolationException e){
            if(e.getMessage().contains(User.UNIQUE_NAME_CONSTRAINT)){
                throw new NameAlreadyTakenException();
            }
        }
    }

    public void changePassword(ChangePasswordRequest changePasswordRequest, String email) {
        User user = userRepository.findByEmail(email).orElseThrow(UserNotFoundException::new);
        if(passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())){
            user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
            userRepository.save(user);
        } else {
            throw new PasswordNotMatchingException();
        }
    }

    public User findByEmail(String ownerEmail) {
        return userRepository.findByEmail(ownerEmail).orElseThrow(UserNotFoundException::new);
    }
}
