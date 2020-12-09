package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsersController {
    @Autowired
    public UsersRepository usersRepository;
    @GetMapping("/person")
    public List<Users> getAllUsers(){
        return this.usersRepository.findAll();
    }
    @PostMapping("/person")
    public Users createUser(@RequestBody Users user){
        return this.usersRepository.save(user);
    }
}
