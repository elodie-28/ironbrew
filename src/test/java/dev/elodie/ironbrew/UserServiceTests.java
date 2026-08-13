package dev.elodie.ironbrew;

import dev.elodie.ironbrew.exception.*;
import dev.elodie.ironbrew.security.user.*;
import dev.elodie.ironbrew.utils.AppUrlProvider;
import dev.elodie.ironbrew.utils.TokenGenerator;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTests {
    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private AppUrlProvider appUrlProvider;

    @Mock
    private TokenGenerator tokenGenerator;

    @InjectMocks
    private UserService userService;

    @Nested
    class RegisterTests {
        @Test
        void shouldRegisterUserSuccessfully() {
            RegisterRequest request = new RegisterRequest();
            request.setName("John Doe");
            request.setPassword("rawPassword");
            request.setEmail("john.doe@example.com");
            request.setEmailPublic(true);
            request.setWebsite("https://www.example.com");

            User mappedUser = new User();
            mappedUser.setName("John Doe");
            mappedUser.setPassword("rawPassword");
            mappedUser.setEmail("john.doe@example.com");
            mappedUser.setEmailPublic(true);
            mappedUser.setWebsite("https://www.example.com");

            User savedUser = new User();
            UUID uuid = UUID.randomUUID();
            savedUser.setId(uuid);
            savedUser.setName("John Doe");
            savedUser.setPassword("encodedPassword");
            savedUser.setEmail("john.doe@example.com");
            savedUser.setEmailPublic(true);
            savedUser.setWebsite("https://www.example.com");

            when(userMapper.toUser(request)).thenReturn(mappedUser);
            when(passwordEncoder.encode("rawPassword")).thenReturn("encodedPassword");
            when(userRepository.save(mappedUser)).thenReturn(savedUser);

            User result = userService.register(request);

            assertNotNull(result);
            assertEquals(uuid, result.getId());
            assertEquals("John Doe", result.getName());
            assertEquals("encodedPassword", result.getPassword());
            assertEquals("john.doe@example.com", result.getEmail());
            assertTrue(result.isEmailPublic());
            assertEquals("https://www.example.com", result.getWebsite());

            verify(userMapper).toUser(request);
            verify(passwordEncoder).encode("rawPassword");
            verify(userRepository).save(mappedUser);
        }

        @Test
        void shouldThrowEmailAlreadyTakenExceptionWhenUniqueConstraintViolated() {
            RegisterRequest request = new RegisterRequest();
            User mappedUser = new User();

            when(userMapper.toUser(request)).thenReturn(mappedUser);
            when(passwordEncoder.encode(any())).thenReturn("encoded");

            when(userRepository.save(any()))
                    .thenThrow(new DataIntegrityViolationException(
                            "constraint " + User.UNIQUE_EMAIL_CONSTRAINT));

            assertThrows(EmailAlreadyTakenException.class,
                    () -> userService.register(request));
        }

        @Test
        void shouldThrowNameAlreadyTakenExceptionWhenUniqueConstraintViolated() {
            RegisterRequest request = new RegisterRequest();
            User mappedUser = new User();

            when(userMapper.toUser(request)).thenReturn(mappedUser);
            when(passwordEncoder.encode(any())).thenReturn("encoded");

            when(userRepository.save(any()))
                    .thenThrow(new DataIntegrityViolationException(
                            "constraint " + User.UNIQUE_NAME_CONSTRAINT));

            assertThrows(NameAlreadyTakenException.class,
                    () -> userService.register(request));
        }
    }

    @Nested
    class CreateConfirmationTokenTests {
        @Test
        void shouldCreateAndSaveConfirmationToken() {
            User user = new User();

            when(tokenGenerator.generateToken()).thenReturn("token");

            String result = userService.createConfirmationToken(user);

            assertNotNull(result);
            assertEquals(result, user.getConfirmationToken());
            assertNotNull(user.getConfirmationSentAt());

            verify(userRepository).save(user);
        }
    }

    @Nested
    class ConfirmTokenTests {
        @Test
        void shouldConfirmUserWhenTokenIsValid() {
            OffsetDateTime sentAt = OffsetDateTime.now().minusHours(2);

            User user = new User();
            user.setConfirmationSentAt(sentAt);
            user.setConfirmationToken("abc");

            when(userRepository.findByConfirmationToken("abc"))
                    .thenReturn(Optional.of(user));

            userService.confirmToken("abc");

            assertNotNull(user.getConfirmedAt());
            assertNull(user.getConfirmationToken());
            assertNull(user.getConfirmationSentAt());

            verify(userRepository).save(user);
        }

        @Test
        void shouldThrowInvalidTokenExceptionWhenTokenIsNull() {
            assertThrows(InvalidTokenException.class,
                    () -> userService.confirmToken(null));
        }

        @Test
        void shouldThrowInvalidTokenExceptionWhenTokenIsEmpty() {
            assertThrows(InvalidTokenException.class,
                    () -> userService.confirmToken(""));
        }

        @Test
        void shouldThrowInvalidTokenExceptionWhenUserNotFound() {
            when(userRepository.findByConfirmationToken("abc"))
                    .thenReturn(Optional.empty());

            assertThrows(InvalidTokenException.class,
                    () -> userService.confirmToken("abc"));
        }

        @Test
        void shouldThrowInvalidTokenExceptionWhenTokenIsOlderThan24Hours() {
            User user = new User();
            user.setConfirmationSentAt(OffsetDateTime.now().minusHours(25));

            when(userRepository.findByConfirmationToken("abc"))
                    .thenReturn(Optional.of(user));

            assertThrows(InvalidTokenException.class,
                    () -> userService.confirmToken("abc"));
        }
    }

    @Nested
    class CreatePasswordResetTokenTests {
        @Test
        void shouldCreatePasswordResetTokenAndSendEmail() throws Exception {
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("user@test.com");

            User user = new User();
            user.setEmail("user@test.com");

            when(userRepository.findByEmail("user@test.com"))
                    .thenReturn(Optional.of(user));

            when(templateEngine.process(anyString(), any(Context.class)))
                    .thenReturn("<html>Email</html>");

            when(appUrlProvider.getAppUrl()).thenReturn("http://localhost/");

            when(tokenGenerator.generateToken()).thenReturn("token");

            MimeMessage mimeMessage = mock(MimeMessage.class);
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);


            userService.createPasswordResetToken(request);

            assertNotNull(user.getPasswordResetToken());
            assertNotNull(user.getPasswordResetSentAt());


            verify(userRepository).save(user);
            verify(mailSender).send(mimeMessage);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserNotFound() {
            ForgotPasswordRequest request = new ForgotPasswordRequest();
            request.setEmail("test@test.com");

            when(userRepository.findByEmail("test@test.com"))
                    .thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.createPasswordResetToken(request));
        }
    }

    @Nested
    class ChangePasswordTests {
        // changePassword(PasswordResetRequest passwordResetRequest)

        @Test
        void shouldChangePasswordAndClearTokenWhenTokenIsValid() {
            PasswordResetRequest request = new PasswordResetRequest();
            request.setToken("valid-token");
            request.setPassword("newPassword");

            User user = new User();
            user.setPasswordResetToken("valid-token");
            user.setPasswordResetSentAt(OffsetDateTime.now().minusMinutes(5));

            when(userRepository.findByPasswordResetToken("valid-token"))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.encode("newPassword"))
                    .thenReturn("encodedPassword");

            userService.changePassword(request);

            assertEquals("encodedPassword", user.getPassword());
            assertNull(user.getPasswordResetToken());
            assertNull(user.getPasswordResetSentAt());

            verify(passwordEncoder).encode("newPassword");
            verify(userRepository).save(user);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenTokenNotFound() {
            PasswordResetRequest request = new PasswordResetRequest();
            request.setToken("invalid-token");

            when(userRepository.findByPasswordResetToken("invalid-token"))
                    .thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.changePassword(request));
        }

        @Test
        void shouldThrowInvalidTokenExceptionWhenTokenExpired() {
            PasswordResetRequest request = new PasswordResetRequest();
            request.setToken("token");

            User user = new User();
            user.setPasswordResetSentAt(OffsetDateTime.now().minusMinutes(16));

            when(userRepository.findByPasswordResetToken("token"))
                    .thenReturn(Optional.of(user));

            assertThrows(InvalidTokenException.class,
                    () -> userService.changePassword(request));
        }

        // changePassword(ChangePasswordRequest changePasswordRequest, String email)

        @Test
        void shouldChangePasswordWhenOldPasswordMatches() {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("correctOldPassword");
            request.setNewPassword("newPassword");

            User user = new User();
            user.setPassword("encodedOldPassword");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches("correctOldPassword", "encodedOldPassword"))
                    .thenReturn(true);

            when(passwordEncoder.encode("newPassword"))
                    .thenReturn("encodedNewPassword");

            userService.changePassword(request, "test@mail.com");

            assertEquals("encodedNewPassword", user.getPassword());
            verify(passwordEncoder).encode("newPassword");
            verify(userRepository).save(user);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("old");
            request.setNewPassword("new");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.changePassword(request, "test@mail.com"));
        }

        @Test
        void shouldThrowPasswordNotMatchingExceptionWhenOldPasswordDoesNotMatch() {
            ChangePasswordRequest request = new ChangePasswordRequest();
            request.setOldPassword("wrongOldPassword");
            request.setNewPassword("newPassword");

            User user = new User();
            user.setPassword("encodedOldPassword");

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));

            when(passwordEncoder.matches("wrongOldPassword", "encodedOldPassword"))
                    .thenReturn(false);

            assertThrows(PasswordNotMatchingException.class,
                    () -> userService.changePassword(request, "test@mail.com"));

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class FindAccountDtoByEmailTests {
        @Test
        void shouldReturnAccountDtoSuccessfullyWhenEmailIsValid() {
            String email = "test@test.com";
            AccountDto accountDto = new AccountDto();
            accountDto.setEmail(email);

            when(userRepository.findAccountDtoByEmail(email))
                    .thenReturn(Optional.of(accountDto));

            AccountDto result = userService.findAccountDtoByEmail(email);

            assertNotNull(result);
            assertEquals(email, result.getEmail());
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenEmailNotFound() {
            String email = "test@test.com";

            when(userRepository.findAccountDtoByEmail(email))
                    .thenReturn(Optional.empty());

            assertThrows(UserNotFoundException.class,
                    () -> userService.findAccountDtoByEmail(email));
        }
    }

    @Nested
    class UpdateFromAccountDtoTests {
        @Test
        void shouldUpdateUserSuccessfully() {
            AccountDto dto = new AccountDto();
            User user = new User();

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));

            userService.updateFromAccountDto(dto, "test@mail.com");

            verify(userMapper).updateFromAccountDto(dto, user);
            verify(userRepository).save(user);
        }

        @Test
        void shouldThrowUserNotFoundExceptionWhenUserDoesNotExist() {
            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.empty());

            AccountDto dto = new AccountDto();

            assertThrows(UserNotFoundException.class,
                    () -> userService.updateFromAccountDto(dto, "test@mail.com"));
        }

        @Test
        void shouldThrowNameAlreadyTakenExceptionWhenUniqueConstraintViolated() {
            AccountDto dto = new AccountDto();
            User user = new User();

            when(userRepository.findByEmail("test@mail.com"))
                    .thenReturn(Optional.of(user));

            when(userRepository.save(user))
                    .thenThrow(new DataIntegrityViolationException(
                            "constraint " + User.UNIQUE_NAME_CONSTRAINT));

            assertThrows(NameAlreadyTakenException.class,
                    () -> userService.updateFromAccountDto(dto, "test@mail.com"));
        }
    }
}
