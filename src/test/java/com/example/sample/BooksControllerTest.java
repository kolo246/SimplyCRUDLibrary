package com.example.sample;

import com.example.sample.books.Books;
import com.example.sample.books.BooksRepository;
import com.example.sample.users.NotFoundException;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Optional;

import static org.mockito.BDDMockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-unit.properties")
public class BooksControllerTest {
    @MockBean
    private BooksRepository booksRepo;
    private final Books book = new Books("Abc","Sienkiewicz",321);

    @Test
    public void testPostUser(){
        when(booksRepo.save(book)).thenReturn(book);
        Books savedBook = booksRepo.save(book);
        Assertions.assertEquals(savedBook.getAuthor(),book.getAuthor());
    }

    @Test
    public void testFindBookById(){
        Long id = 1L;
        when(booksRepo.findById(id)).thenReturn(Optional.of(book));
        Books foundBook = booksRepo.findById(id).get();
        Assertions.assertNotNull(foundBook);
    }

    @Test
    public void testFindByIdNotFound(){
        Long id = 1L;
        when(booksRepo.findById(id)).thenThrow(new NotFoundException());
    }

    @Test
    public void testFindBooksByAuthor(){
        String author = "Sienkiewicz";
        when(booksRepo.findBooksByAuthor(author)).thenReturn(Optional.of(book));
        Optional<Books> foundBook = booksRepo.findBooksByAuthor(author);
        Assertions.assertEquals(book.getAuthor(),foundBook.get().getAuthor());
    }

    @Test
    public void testFindBookByAuthorNotFound(){
        String author = "Sienkiewicz";
        when(booksRepo.findBooksByAuthor(author)).thenThrow(new NotFoundException());
    }
}