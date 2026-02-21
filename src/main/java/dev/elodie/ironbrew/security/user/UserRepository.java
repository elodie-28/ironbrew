package dev.elodie.ironbrew.security.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.security.core.parameters.P;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByConfirmationToken(String confirmationToken);

    Optional<User> findByPasswordResetToken(String passwordResetToken);

    @Query("SELECT email, name, isEmailPublic, website from User where email = :email")
    Optional<AccountDto> findAccountDtoByEmail(@P("email") String email);
}
