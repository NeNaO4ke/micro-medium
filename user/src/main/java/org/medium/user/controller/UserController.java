package org.medium.user.controller;

import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.medium.proto.UserProto;
import org.medium.user.domain.User;
import org.medium.user.service.GrpcService;
import org.medium.user.service.UserService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final GrpcService grpcService;

    @Timed(value = "time.getting.user.by.id")
    @GetMapping("/{id}")
    public Mono<User> getUserByID(@PathVariable String id) {
        return userService.findUserById(id);
    }

    @Timed(value = "time.create.user")
    @PostMapping(value = "/")
    public Mono<User> createUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    @GetMapping(value = "/proto/{id}", produces = "application/x-protobuf")
    public Mono<UserProto.User> getUserByIDProto(@PathVariable String id) {
        return userService.findUserByIdProto(id);
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

    @GetMapping("/avatar")
    public Mono<String> getRandomAvatarUrl(@RequestParam String username, @RequestParam String firstname, @RequestParam String lastname) {
        return Mono.just(grpcService.getAvatarUrl(username, firstname, lastname));
    }


    @DeleteMapping(value = "/{id}")
    public Mono<Void> deleteUser(@PathVariable String id) { return userService.deleteUser(id);}

}
