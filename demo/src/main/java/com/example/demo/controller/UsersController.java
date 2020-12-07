package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsersController {

    @Autowired
    public UsersRepository usersRepository;

    @GetMapping("users")
    public List<Users> getAllUsers(){
        return this.usersRepository.findAll();
    }

    @PostMapping("users")
    public Users createUser(@RequestBody Users user){
        return this.usersRepository.save(user);
    }


}
