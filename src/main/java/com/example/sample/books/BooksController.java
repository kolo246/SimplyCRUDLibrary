package com.example.sample.books;

import com.example.sample.extra.PatchObject;
import com.example.sample.exceptions.NotFoundException;
import com.example.sample.users.Users;
import com.example.sample.users.UsersRepository;
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
@RequestMapping(value = "/api")
public class BooksController {
    private final BooksRepository booksRepo;
    private final BooksPagingRepository pagingRepo;
    private final UsersRepository usersRepo;
    public static final String defaultPages = "0";
    public static final String defaultSize = "5";

    @Autowired
    public BooksController(BooksRepository booksRepo, BooksPagingRepository pagingRepo, UsersRepository usersRepo) {
        this.booksRepo = booksRepo;
        this.pagingRepo = pagingRepo;
        this.usersRepo = usersRepo;
    }

    private Books applyBooksToPatch(JsonPatch patch, Books targetBook) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetBook, JsonNode.class));
        return objectMapper.treeToValue(patched, Books.class);
    }

    @PatchMapping(value = "/books/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<Books> updateBookTitleByAuthor(@PathVariable("id") Long id,
                                                         @RequestBody JsonPatch patch) {
        try {
            Books bookToUpdate = booksRepo.findById(id).orElseThrow(() -> new NotFoundException("Not found book with id "+ id));
            PatchObject<Books> patchObject = new PatchObject<>(bookToUpdate);
            Books patchedBook = patchObject.applyPatchObject(patch);
            booksRepo.save(patchedBook);
            return ResponseEntity.status(HttpStatus.OK).body(patchedBook);
        } catch (JsonPatchException | JsonProcessingException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping(value = "/books")
    public ResponseEntity<Books> postBooks(@RequestBody Books book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(book);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Books findBookById(@PathVariable("id") Long id) {
        return booksRepo.findById(id).orElseThrow(() -> new NotFoundException("Not found book with id "+ id));
    }

    @GetMapping(value = "/books{pages}{size}/{borrow}", params = {"pages", "size","borrow"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Books> findAllBooks(
            @RequestParam(value = "pages", defaultValue = defaultPages) int pages,
            @RequestParam(value = "size", defaultValue = defaultSize) int size,
            @RequestParam(value = "borrow", defaultValue = "false") boolean isBorrow) {
        PageRequest pageRequest = PageRequest.of(pages, size);
        return pagingRepo.findAll(pageRequest);
    }

    @PutMapping(value = "/books/{id_books}/reserve/{user_id}")
    public ResponseEntity<Books> reserveBook(@PathVariable("id_books") Long id_books,
                             @PathVariable("user_id") Long user_id) {
        Books reserveBook = booksRepo.findById(id_books).orElseThrow(() -> new NotFoundException("Not found book with id "+ id_books));
        if (reserveBook.getUser_id() != null) {
            return ResponseEntity.unprocessableEntity().body(reserveBook);
        }
        Users user = usersRepo.findByIdAndDeletedIsFalse(user_id).orElseThrow(() -> new NotFoundException("Not found user with id "+ user_id));
        reserveBook.setUser_id(user);
        return ResponseEntity.status(HttpStatus.OK).body(reserveBook);
    }

    @DeleteMapping(value = "/books/{id}")
    public ResponseEntity<String> deleteBookById(@PathVariable("id") Long id) {
        Books bookToDelete = booksRepo.findById(id).orElseThrow(() -> new NotFoundException("Not found books with id "+ id));
        bookToDelete.setDeleted(true);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
