package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsersController{
    @Autowired
    private UsersRepository usersRepository;
    @GetMapping("get_users")
    public List<Users> getAllUsers() {
        return this.usersRepository.findAll();
    }
    @PostMapping("post_user")
    public Users postUser(@RequestBody Users user){
        return this.usersRepository.save(user);
    }
}