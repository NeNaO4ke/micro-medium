package org.medium.user.service;

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
        return userRepository.findById(id);
    }

    public Mono<User> saveUser(User user) {
        return userRepository.save(user);
    }

    public Mono<Void> deleteUser(String id) {
        return userRepository.deleteById(id);
    }


    public Flux<User> getAllUsersByIds(List<String> ids) {
        return userRepository.findUsersByIdIn(ids);
    }


}
