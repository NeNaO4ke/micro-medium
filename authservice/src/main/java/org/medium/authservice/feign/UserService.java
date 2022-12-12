package org.medium.authservice.feign;

import org.medium.authservice.domain.UserDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "user-service")
public interface UserService {

    @GetMapping(value = "/user/{id}")
    Mono<UserDTO> getUserById(@PathVariable String id);

    @GetMapping(value = "/user/username/{username}")
    Mono<UserDTO> getUserByUsername(@PathVariable String username);
}
