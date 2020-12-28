package com.example.demo.controller;

import com.example.demo.model.Users;
import com.example.demo.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class UsersController {
    public final UsersRepository usersRepository;

    public UsersController(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }
    @GetMapping("/person")
    public List<Users> getAllUsers(){
        return this.usersRepository.findAll();
    }
    @PostMapping("/person")
    public Users createUser(@RequestBody Users user){
        return this.usersRepository.save(user);
    }
}