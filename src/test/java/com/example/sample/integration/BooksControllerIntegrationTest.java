package com.example.sample.integration;

import com.example.sample.books.Books;
import com.example.sample.books.BooksController;
import com.example.sample.books.BooksRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = ConfigIntegrationContainer.class)
@AutoConfigureMockMvc
@TestPropertySource("classpath:application-integration.properties")
@Ignore
public class BooksControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private BooksRepository booksRepo;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testPagination() throws Exception {
        insertBooks();
        //test default values, size in both objects should be 5
        MvcResult result = mockMvc.perform(get("/api/books?pages?size")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        List<Books> listBooks = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>(){});
        Assertions.assertEquals(Integer.parseInt(BooksController.defaultSize), listBooks.size());
    }


    private void insertBooks(){
        booksRepo.save(new Books("Hobbit","Tolkien", 438));
        booksRepo.save(new Books("Lalka","Prus", 123));
        booksRepo.save(new Books("Symfonia","Grebosz", 326));
        booksRepo.save(new Books("Gommorra","Saviano", 174));
        booksRepo.save(new Books("Ogniem i Mieczem","Sienkiewicz", 253));
        booksRepo.save(new Books("Zemstam","Fredro", 183));
        booksRepo.save(new Books("Pan Tadeusz","Adam Mickiewicz", 458));
    }
}
