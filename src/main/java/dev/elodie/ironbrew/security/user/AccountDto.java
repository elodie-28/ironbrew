package dev.elodie.ironbrew.security.user;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import org.springframework.data.annotation.PersistenceCreator;

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

    public AccountDto() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isEmailPublic() {
        return isEmailPublic;
    }

    public void setEmailPublic(boolean emailPublic) {
        isEmailPublic = emailPublic;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }
}
