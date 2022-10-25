package org.medium.article.feign;

import org.medium.article.domain.UserDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ReactiveFeignClient(name = "user-service")
public interface UserService {

    @GetMapping("/user/{id}")
    public Mono<UserDTO> getUserByID(@PathVariable String id);

    @PostMapping(value = "/user/ids")
    public Flux<UserDTO> getUsersByIds(@RequestBody List<String> ids);
}
