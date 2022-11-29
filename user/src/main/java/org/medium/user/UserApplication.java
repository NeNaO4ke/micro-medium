package org.medium.user;

import io.sentry.Sentry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApplication {

    public static void main(String[] args) {
        Sentry.withScope(scope -> {
            scope.setTransaction("Starting application");
            Sentry.captureMessage("User medium started");
        });
        SpringApplication.run(UserApplication.class, args);
    }

}
