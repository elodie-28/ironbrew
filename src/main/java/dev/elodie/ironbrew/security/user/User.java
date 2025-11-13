package dev.elodie.ironbrew.security.user;

import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.OffsetDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = User.UNIQUE_EMAIL_CONSTRAINT, columnNames = {"email"})
        }
)
public class User implements UserDetails {
    public static final String UNIQUE_EMAIL_CONSTRAINT = "email_users_uk";

    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;

    private String name;

    private String password;

    private String website;

    @ColumnDefault("false")
    private boolean isEmailPublic = false;

    private String confirmationToken;

    private OffsetDateTime confirmedAt;

    private OffsetDateTime confirmationSentAt;

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getWebsite() {
        return website;
    }

    public void setEmailPublic(boolean isEmailPublic) {
        this.isEmailPublic = isEmailPublic;
    }

    public boolean isEmailPublic() {
        return isEmailPublic;
    }

    public void setConfirmationToken(String confirmationToken) {
        this.confirmationToken = confirmationToken;
    }

    public String getConfirmationToken() {
        return confirmationToken;
    }

    public void setConfirmationSentAt(OffsetDateTime confirmationSentAt) {
        this.confirmationSentAt = confirmationSentAt;
    }

    public OffsetDateTime getConfirmationSentAt() {
        return confirmationSentAt;
    }

    public void setConfirmedAt(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getUsername() {
        return name;
    }

    @Override
    public boolean isEnabled(){
        return confirmedAt != null;
    }
}
