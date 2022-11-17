package org.medium.user.controller;

import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.medium.user.domain.User;
import org.medium.user.service.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.medium.user.async.AsyncConfiguration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
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
    @Async("asyncExecutor")
    public List<User> getUsersByIds(@RequestBody List<String> ids) throws ExecutionException, InterruptedException {
        ArrayList<CompletableFuture> futures = new ArrayList<CompletableFuture>();
        for(int i=0; i < ids.size(); i++) {
            CompletableFuture<User> user = userService.findUserById(ids.get(i)).toFuture();
            futures.add(user);
        }
        CompletableFuture[] arrayFutures = new CompletableFuture[futures.size()];
        futures.toArray(arrayFutures);
        CompletableFuture.allOf(arrayFutures).join();
        ArrayList<User> users = new ArrayList<User>();
        for (int i=0; i < arrayFutures.length; i++) {
            users.add((User) arrayFutures[i].get());
        }
        return users;
    }


    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) { return userService.deleteUser(id);}

}
