package dev.elodie.ironbrew.utils;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Component
public class AppUrlProvider {
    public String getAppUrl() {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath().build()
                .toUriString();
    }
}
