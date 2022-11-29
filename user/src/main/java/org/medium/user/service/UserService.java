package org.medium.user.service;

import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.medium.user.domain.User;
import org.medium.user.repository.UserRepository;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Mono<User> findUserById(String id) {
        Sentry.withScope(scope -> {
            scope.setTransaction("Getting user");
            scope.setTag("user_service", "find_user_by_id");
            Sentry.captureMessage(String.format("Getting user with id %s", id));
        });
        return userRepository.findById(id);
    }

    public Mono<User> saveUser(User user) {
        Sentry.withScope(scope -> {
            scope.setTag("user_service", "save_user");
            Sentry.captureMessage(String.format("Saving user with name %s", user.getFirstName()));
        });
        return userRepository.save(user);
    }

    public Mono<Void> deleteUser(String id) {
        Sentry.configureScope(scope -> {
            scope.setTransaction("Deleting user");
            scope.setTag("user_service", "save_user");
        });
        Sentry.captureMessage(String.format("Deleting user with id %s", id));
        return userRepository.deleteById(id);
    }


    public Flux<User> getAllUsersByIds(List<String> ids) {
        return userRepository.findUsersByIdIn(ids);
    }


}
