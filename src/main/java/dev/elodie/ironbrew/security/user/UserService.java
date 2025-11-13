package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.exception.EmailAlreadyTakenException;
import dev.elodie.ironbrew.exception.InvalidTokenException;
import dev.elodie.ironbrew.exception.NameAlreadyTakenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
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

    public String createToken(User user) {
        String token = UUID.randomUUID().toString();
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

        if(user.getConfirmedAt() == null){
            user.setConfirmedAt(OffsetDateTime.now());
        }

        userRepository.save(user);
    }
}
