package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class UsersController{
    @Autowired
    private UsersRepository usersRepository;
    @GetMapping("users")
    public List<Users> getAllUsers() {
        return this.usersRepository.findAll();
    }
    @PostMapping("users")
    public Users postUser(@RequestBody Users user){
        return this.usersRepository.save(user);
    }
    @GetMapping("users/{id}")
    public Optional<Users> findById(@PathVariable long id){
        return this.usersRepository.findById(id);
    }
    @DeleteMapping("delete/{id}")
    public void deleteById(@PathVariable long id){
        this.usersRepository.deleteById(id);
    }
}