package com.example.sample.integration;

import com.example.sample.books.Books;
import com.example.sample.books.BooksPagingRepository;
import com.example.sample.books.BooksRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Objects;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ConfigIntegrationContainer.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-integration.properties")
@ComponentScan("com.example.sample")
public class BooksControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BooksRepository booksRepo;
    @Autowired
    private BooksPagingRepository pagingRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setUpData() {
        insertBooks();
    }

    @After
    public void cleanDB() {
        booksRepo.deleteAll();
    }

    @Test
    public void testPatchBook() throws Exception {
        String patchBody = "[\n" +
                "    {\n" +
                "        \"op\":\"replace\",\n" +
                "        \"path\":\"/author\",\n" +
                "        \"value\":\"Prus\"\n" +
                "    }\n" +
                "]";
        Iterable<Books> books = booksRepo.findAll();
        Books foundBook = booksRepo.findById(books.iterator().next().getId()).get();
        MvcResult result = mockMvc.perform(patch("/api/books/{id}", foundBook.getId())
                .contentType("application/json-patch+json")
                .content(patchBody))
                .andExpect(status().isOk())
                .andReturn();
        Books patchedBook = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
        });
        foundBook.setAuthor("Prus");
        Assertions.assertEquals(foundBook, patchedBook);
    }

    @Test
    public void testFindBooksById() throws Exception {
        Iterable<Books> books = booksRepo.findAll();
        Books foundBook = booksRepo.findById(books.iterator().next().getId()).get();
        MvcResult result = mockMvc.perform(get("/api/books/{id}", foundBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Books resultBook = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals(foundBook, resultBook);
    }

    @Test
    public void testNotFindBookById() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/books/{id}", 0)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Not found book with id 0", Objects.requireNonNull(result.getResolvedException()).getMessage());
    }

    @Test
    public void testPagination() throws Exception {
        //test default values for pages and size
        MvcResult result = mockMvc.perform(get("/api/books")
                .param("pages", "")
                .param("size", "")
                .param("borrow", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Books> sizeDefault = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals(5, sizeDefault.size());
        //test random values for pages and size
        MvcResult randomResult = mockMvc.perform(get("/api/books")
                .param("pages", "0")
                .param("size", "7")
                .param("borrow", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Books> sizeRandom = objectMapper.readValue(randomResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals(7, sizeRandom.size());
        //test sort by ID
        MvcResult sortResult = mockMvc.perform(get("/api/books")
                .param("pages", "0")
                .param("size", "7")
                .param("borrow", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Books> sortList = objectMapper.readValue(sortResult.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Pageable page = PageRequest.of(0, 7, Sort.by("id"));
        List<Books> pagingList = pagingRepo.findAllByBorrowIsFalse(false, page);
        Assertions.assertEquals(pagingList.get(0).getId(), sortList.get(0).getId());
        //test all not borrow books
        Books borrowedBook = pagingList.get(4);
        borrowedBook.setBorrow(true);
        booksRepo.save(borrowedBook);
        MvcResult notBorrow = mockMvc.perform(get("/api/books")
                .param("pages", "0")
                .param("size", "7")
                .param("borrow", "false")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Books> listNotBorrow = objectMapper.readValue(notBorrow.getResponse().getContentAsString(), new TypeReference<>() {});
        List<Books> expectedList = pagingRepo.findAllByBorrowIsFalse(false, PageRequest.of(0,7,Sort.by("id")));
        Assertions.assertEquals(expectedList.size(), listNotBorrow.size());
        //test all borrow books
        borrowedBook = pagingList.get(0);
        borrowedBook.setBorrow(true);
        booksRepo.save(borrowedBook);
        MvcResult borrowResult = mockMvc.perform(get("/api/books")
                .param("pages", "0")
                .param("size", "7")
                .param("borrow", "true")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Books> resultBook = objectMapper.readValue(borrowResult.getResponse().getContentAsString(), new TypeReference<>() {});
        List<Books> expectedBorrowSize = pagingRepo.findAllByBorrowIsFalse(true, PageRequest.of(0,7,Sort.by("id")));

        Assertions.assertEquals(expectedBorrowSize.size(), resultBook.size());
        //test all books
        MvcResult allResult = mockMvc.perform(get("/api/books")
                .param("pages", "0")
                .param("size", "7")
                .param("borrow", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Books> allBooks = objectMapper.readValue(allResult.getResponse().getContentAsString(), new TypeReference<>() {});
        List<Books> expectedBooks = pagingRepo.findAll(PageRequest.of(0,7,Sort.by("id")));
        Assertions.assertEquals(expectedBooks.size(), allBooks.size());

    }

    @Test
    public void testInsertBook() throws Exception {
        Books insertBook = new Books("Wesel", "Wyspianski",526);
        MvcResult insertResult = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(insertBook)))
                .andExpect(status().isCreated())
                .andReturn();
        Books resultBook = objectMapper.readValue(insertResult.getResponse().getContentAsString(), new TypeReference<>(){});
        insertBook.setId(resultBook.getId());
        Assertions.assertEquals(insertBook,resultBook);
    }

    @Test
    public void testDeleteBookById() throws Exception {
        Iterable<Books> books = booksRepo.findAll();
        Books foundBook = booksRepo.findById(books.iterator().next().getId()).get();
        mockMvc.perform(delete("/api/books/{id}", foundBook.getId())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Books deletedBook = booksRepo.findById(foundBook.getId()).get();
        Assertions.assertTrue(deletedBook.isDeleted());
    }

    private void insertBooks() {
        booksRepo.save(new Books("Hobbit", "Tolkien", 438));
        booksRepo.save(new Books("Lalka", "Prus", 123));
        booksRepo.save(new Books("Symfonia", "Grebosz", 326));
        booksRepo.save(new Books("Gommorra", "Saviano", 174));
        booksRepo.save(new Books("Ogniem i Mieczem", "Sienkiewicz", 253));
        booksRepo.save(new Books("Zemstam", "Fredro", 183));
        booksRepo.save(new Books("Pan Tadeusz", "Adam Mickiewicz", 458));
    }
}
