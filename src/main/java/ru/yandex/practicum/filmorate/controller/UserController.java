package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;

import java.util.Collection;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController implements Controller<User> {

    private UserService userService;

    public UserController() {
    }

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }


    @Override
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getAll() {
        return userService.findAll();
    }

    @Override
    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public User getById(@PathVariable("userId") long id) {
        return userService.findById(id)
                .orElseThrow(() -> new NullPointerException("Нет user с заданным ID"));
    }

    @GetMapping("/{userId}/friends")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getFriends(@PathVariable("userId") long id) {
        return userService.getFriends(id);
    }

    @GetMapping("/{userId}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> getCommonFriends(@PathVariable("userId") long userId,
                                             @PathVariable("otherId") long otherId) {
        return userService.getCommonFriends(userId, otherId);
    }

    @Override
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody final User user) {
        return userService.create(user).get();
    }

    @Override
    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@Valid @RequestBody final User user) {
        return userService.update(user).get();
    }

    @PutMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public User addFriends(@PathVariable("userId") long userId,
                           @PathVariable("friendId") long friendId) {
        return userService.addFriend(userId, friendId).get();
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public User deleteFriends(@PathVariable("userId") long userId,
                              @PathVariable("friendId") long friendId) {
        return userService.deleteFriend(userId, friendId).orElseThrow(() -> new NullPointerException());
    }


}
