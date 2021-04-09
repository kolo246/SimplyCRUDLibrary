package com.example.sample.users;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
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

    @Autowired
    public UsersController(UsersRepository usersRepo, PagingRepository pagingRepo){
        this.usersRepo = usersRepo;
        this.pagingRepo = pagingRepo;
    }

    private Users applyUsersToPatch(JsonPatch patch, Users targetUser) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
        return objectMapper.treeToValue(patched, Users.class);
    }

    @GetMapping(value = "/users{pages}{size}", params = {"pages","size"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Users> getUsers(@RequestParam (value = "pages", defaultValue = "0") int pages,
                                @RequestParam (value = "size", defaultValue = "3") int size){
        PageRequest page = PageRequest.of(pages,size);
        return pagingRepo.findAll(page);
    }

    @RequestMapping(value = "/users{id}", params = "id", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Users getUserById(@RequestParam(value = "id", required = false) Long id){
        return usersRepo.findByIdAndDeletedIsFalse(id)
                .orElseThrow(NotFoundException::new);
    }

    @RequestMapping(value = "/users{name}", params = "name", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Users getUserByName(@RequestParam(value = "name", required = false) String name){
        return usersRepo.findByNameAndDeletedIsFalse(name)
                .orElseThrow(NotFoundException::new);
    }

    @PostMapping(value = "/users", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Users postUser(@RequestBody Users user){
        return usersRepo.save(user);
    }

    @PatchMapping(path = "/users{id}", consumes = "application/json+patch")
    public ResponseEntity<Users> updateUsers(@RequestParam(value = "id") Long id, @RequestBody JsonPatch patch){
        try{
            Users user = usersRepo.findByIdAndDeletedIsFalse(id).orElseThrow(NotFoundException::new);
            Users userPatch = applyUsersToPatch(patch,user);
            usersRepo.save(userPatch);
            return ResponseEntity.ok(userPatch);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/users{id}")
    public Users deleteUserById(@RequestParam("id") Long id){
        Users user = usersRepo.findByIdAndDeletedIsFalse(id).orElseThrow(NotFoundException::new);
        user.setDeleted(true);
        return usersRepo.save(user);
    }
}