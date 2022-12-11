package org.medium.user.service;

import io.grpc.StatusRuntimeException;
import io.sentry.Sentry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.medium.proto.UserProto;
import org.medium.user.domain.User;
import org.medium.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GrpcService grpcService;

    public Mono<User> findUserById(String id) {
        Sentry.withScope(scope -> {
            scope.setTransaction("Getting user");
            scope.setTag("user_service", "find_user_by_id");
            Sentry.captureMessage(String.format("Getting user with id %s", id));
        });
        return userRepository.findById(id);
    }

    public Mono<UserProto.User> findUserByIdProto(String id) {
        return userRepository.findById(id)
                .map(u -> {
                    UserProto.User.Builder user = UserProto.User.newBuilder()
                            .setId(u.getId())
                            .setUsername(u.getUsername());
                    if (Objects.nonNull(u.getFirstName())) {
                        user.setFirstName(u.getFirstName());
                    }
                    if (Objects.nonNull(u.getLastName())) {
                        user.setFirstName(u.getLastName());
                    }
                    if (Objects.nonNull(u.getAvatarUrl())) {
                        user.setFirstName(u.getAvatarUrl());
                    }
                    return user.build();
                });
    }


    public Mono<User> saveUser(User user) {
        if (Objects.isNull(user.getAvatarUrl())) {
            try {
                String avatarUrl = grpcService.getAvatarUrl(user.getUsername(), user.getFirstName(), user.getLastName());
                user.setAvatarUrl(avatarUrl);
            } catch (StatusRuntimeException e) {
                log.error("Failed grpc with code {}", e.getStatus().getCode());
            }
        }
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
