package com.proiectfinal.bankapp.controller;

import com.proiectfinal.bankapp.domain.User;
import com.proiectfinal.bankapp.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User createUser (@RequestBody User user){
        return userService.createUser(user);
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User findUserById (@PathVariable long id) {
       return userService.findUserById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<User> findAllUsers(){
        return userService.findAllUsers();
    }


    @PutMapping ("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update (@PathVariable("id") long id, @RequestBody User user ){
        userService.updateFields(id, user);
    }






}
