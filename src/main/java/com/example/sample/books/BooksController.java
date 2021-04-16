package com.example.sample.books;

import com.example.sample.users.NotFoundException;
import com.example.sample.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping(value = "/books{pages}{size}")
    public List<Books> findAllBooks(
            @RequestParam (value = "pages", defaultValue = "0") int pages,
            @RequestParam (value = "size", defaultValue = "5") int size){
        PageRequest pageRequest = PageRequest.of(pages,size);
        return pagingRepo.findAll(pageRequest);
    }
}
