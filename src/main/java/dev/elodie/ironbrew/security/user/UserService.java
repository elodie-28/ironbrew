package dev.elodie.ironbrew.security.user;

import dev.elodie.ironbrew.exception.EmailAlreadyTakenException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    public void register(RegisterRequest registerRequest) {
        if(registerRequest.getWebsite().isEmpty()){
            registerRequest.setWebsite(null);
        }

        User user = userMapper.toUser(registerRequest);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        try{
            userRepository.save(user);
        }catch(DataIntegrityViolationException e){
            if(e.getMessage().contains(User.UNIQUE_EMAIL_CONSTRAINT)){
                throw new EmailAlreadyTakenException();
            }
        }
    }
}
