package org.medium.article.feign;

import io.micrometer.core.annotation.Counted;
import org.medium.article.domain.UserDTO;
import org.medium.proto.UserProto;
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

    @Counted(value = "count.getUserById", description = "Number of times getUserById method was called")
    @GetMapping("/user/{id}")
    Mono<UserDTO> getUserByID(@PathVariable String id);

    @GetMapping("api/user/proto/{id}")
    Mono<UserProto.User> getUserByIDProto(@PathVariable String id);

    @Counted(value = "count.getUsersById", description = "Number of times getUsersByIdMethod was called")
    @PostMapping(value = "/user/ids")
    Flux<UserDTO> getUsersByIds(@RequestBody List<String> ids);
}
