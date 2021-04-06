package com.example.sample.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class UsersController {
    private final UsersRepository usersRepo;
    private final PagingRepository pagingRepo;

    @Autowired
    public UsersController(UsersRepository usersRepo, PagingRepository pagingRepo){
        this.usersRepo = usersRepo;
        this.pagingRepo = pagingRepo;
    }
    @GetMapping("/users")
    public List<Users> getUsers(){
        return usersRepo.findAll();
    }
    @RequestMapping(value = "/users/id/{id}", method = RequestMethod.GET)
    public Users getUserById(@PathVariable ("id") Long id){
        return usersRepo.findById(id).orElseThrow(NotFoundException::new);
    }
    @RequestMapping(value = "/users/name/{name}", method = RequestMethod.GET)
    public Users getUserByName(@PathVariable String name){
        return usersRepo.findByName(name)
                .orElseThrow(NotFoundException::new);
    }
    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Users postUser(@RequestBody Users user){
        return usersRepo.save(user);
    }
    @GetMapping("/users/page")
    public List<Users> getFiveUsers(){
        PageRequest page = PageRequest.of(0,5 , Sort.by("age").descending());
        return pagingRepo.findAll(page).getContent();
    }
    @PatchMapping(path = "/users/{id}")
    public Users usersPatch(@PathVariable("id") Long id,
                           @RequestBody Users patch){
        Users user = usersRepo.findById(id).get();
        if (patch.getName() != null){
            user.setName(patch.getName());
        }
        if (patch.getEmail() != null){
            user.setEmail(patch.getEmail());
        }
        if (patch.getPhoneNumber() != null){
            user.setPhoneNumber(patch.getPhoneNumber());
        }
        if (patch.getAge() != null){
            user.setAge(patch.getAge());
        }
        return usersRepo.save(user);
    }
    @DeleteMapping("/users/{id}")
    public Users deleteUserById(@PathVariable("id") Long id){
        Users user = usersRepo.findById(id).get();
        user.setDeleted(true);
        return usersRepo.save(user);
    }
}
