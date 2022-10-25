package org.medium.user.repository;

import org.medium.user.domain.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.List;

public interface UserRepository extends ReactiveCrudRepository<User, String> {

    Flux<User> findUsersByIdIn(List<String> ids);
}
