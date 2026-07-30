package dev.bass631.spendy.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "auth")
public class AppUsersProperties {

    private List<UserEntry> users = new ArrayList<>();

    @Data
    public static class UserEntry {
        private String username;
        private String password;
    }
}
