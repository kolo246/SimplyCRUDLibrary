package com.example.sample.books;

import com.example.sample.users.NotFoundException;
import com.example.sample.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class BooksController {
    private final BooksRepository booksRepo;
    private final BooksPagingRepository pagingRepo;

    @Autowired
    public BooksController(BooksRepository booksRepo, BooksPagingRepository pagingRepo) {
        this.booksRepo = booksRepo;
        this.pagingRepo = pagingRepo;
    }

    @PostMapping(value = "/books/{id}")
    public Books postBooks(@RequestBody Books book) {
        return booksRepo.save(book);
    }

    @GetMapping(value = "/books/{id}")
    public Books findBookById(@PathVariable("id") Long id) {
        return booksRepo.findById(id).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/books/{author}")
    public Books findBookByAuthor(@PathVariable("author") String author) {
        return booksRepo.findBooksByAuthor(author).orElseThrow(NotFoundException::new);
    }

    @GetMapping(value = "/books{pages}{size}", params = {"pages","size"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Books> findAllBooks(
            @RequestParam (value = "pages", defaultValue = "0") int pages,
            @RequestParam (value = "size", defaultValue = "5") int size){
        PageRequest pageRequest = PageRequest.of(pages,size);
        return pagingRepo.findAll(pageRequest);
    }

    @GetMapping(value = "/books/{author}", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Books> findBooksByAuthorWherePagesIsGraterThan100(@PathVariable ("author") String author){
        Pageable pageable = PageRequest.of(0, 5);
        return pagingRepo.findBooksByAuthorAndWherePagesIsGreaterThan(author, pageable);
    }
}
