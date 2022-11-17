package org.medium.user.controller;

import lombok.RequiredArgsConstructor;
import org.apache.http.entity.ContentType;
import org.medium.user.domain.User;
import org.medium.user.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import org.springframework.ui.Model;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserPageController {

    private final UserService userService;

    @GetMapping("/{id}")
    public String getUser(@PathVariable("id") String id, Model model) {
        Mono<User> user = userService.findUserById(id);
        model.addAttribute("user", user);
        return "user";
    }

}
