package org.example.rest;


import lombok.AllArgsConstructor;
import org.example.core.R;
import org.example.entity.User;
import org.example.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("user")
@AllArgsConstructor
public class UserRest {

    private final UserService userService;

    @GetMapping("getUserByName")
    public Mono<ResponseEntity<Mono<User>>> getUserByName(@RequestParam("username") String username){
        return Mono.just(ResponseEntity.ok(userService.findUserByName(username)));
    }

    @GetMapping(value = "all")
    public Mono<R<List<User>>> getAllUser() {
        return userService.getAllUserInfo().collectList().map(R::success);
    }

    @GetMapping(value = "getUserById/{id}")
    public Mono<R<User>> getUserById(@PathVariable("id") Integer id) {
        return userService.getUserById(id).map(R::success);
    }

    @PostMapping(value = "save")
    public Mono<R> saveUser(@RequestBody User user) {
        return userService.saveUser(user).thenReturn(R.success());
    }

    @PostMapping(value = "update")
    public Mono<R> updateUser(@RequestBody User user) {
        return userService.updateUser(user).thenReturn(R.success());
    }

    @DeleteMapping(value = "delete/{id}")
    public Mono<R> deleteUser(@PathVariable("id") Integer id) {
        return userService.deleteUser(id).thenReturn(R.success());
    }

}
