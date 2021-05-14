package com.tango.down.users;

import com.tango.down.extra.PatchObject;
import com.tango.down.exceptions.NotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class UsersController {
    private final UsersRepository usersRepo;
    private final PagingRepository pagingRepo;
    public static final String defaultPages = "0";
    public static final String defaultSize = "5";

    public UsersController(UsersRepository usersRepo, PagingRepository pagingRepo) {
        this.usersRepo = usersRepo;
        this.pagingRepo = pagingRepo;
    }

    @RequestMapping(value = "/users", params = {"pages", "size"}, produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.GET)
    public List<Users> getUsers(@RequestParam(required = false, value = "pages", defaultValue = defaultPages) int pages,
                                @RequestParam(required = false, value = "size", defaultValue = defaultSize) int size) {
        PageRequest page = PageRequest.of(pages, size, Sort.by(Sort.Order.asc("name")));
        return pagingRepo.findAll(page);
    }

    @RequestMapping(value = "/users/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Users getUserById(@PathVariable(value = "id") Long id) {
        return usersRepo.findByIdAndDeletedIsFalse(id)
                .orElseThrow(() -> new NotFoundException("Not found user with id " + id));
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Users> postUser(@RequestBody Users user) {
        //what if meeting db connection !!
        return ResponseEntity.status(HttpStatus.CREATED).body(usersRepo.save(user));
    }

    @PatchMapping(path = "/users/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Users> updateUsers(@PathVariable(value = "id") Long id, @RequestBody JsonPatch patch) {
        try {
            Users user = usersRepo.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
            PatchObject<Users> patchObject = new PatchObject<>(user);
            Users userPatch = patchObject.applyPatchObject(patch);
            usersRepo.save(userPatch);
            return ResponseEntity.ok(userPatch);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Users> deleteUserById(@PathVariable("id") Long id) {
        Users user = usersRepo.findByIdAndDeletedIsFalse(id).orElseThrow(() -> new NotFoundException("Not found user with id " + id));
        user.setDeleted(true);
        usersRepo.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }
}