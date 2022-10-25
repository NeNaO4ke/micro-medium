package org.medium.user.controller;

import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.medium.user.domain.User;
import org.medium.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    public Mono<User> getUserByID(@PathVariable String id) {
        return userService.findUserById(id);
    }

    @PostMapping(value = "/")
    public Mono<User> createUser(@RequestBody User user) {
       return userService.saveUser(user);
    }

    @PostMapping(value = "/ids")
    public Flux<User> getUsersByIds(@RequestBody List<String> ids) {
        return userService.getAllUsersByIds(ids);
    }

    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) {
        return userService.deleteUser(id);
    }

}
