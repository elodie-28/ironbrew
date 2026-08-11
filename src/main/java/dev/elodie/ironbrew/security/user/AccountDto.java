package dev.elodie.ironbrew.security.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.PersistenceCreator;

@Setter
@Getter
@NoArgsConstructor
public class AccountDto {
    private String email;
    @NotBlank(message = "Name is required")
    private String name;
    private boolean isEmailPublic;
    @URL(message = "Please enter a valid URL")
    private String website;

    @PersistenceCreator
    public AccountDto(String email, String name, boolean isEmailPublic, String website) {
        this.email = email;
        this.name = name;
        this.isEmailPublic = isEmailPublic;
        this.website = website;
    }
}
