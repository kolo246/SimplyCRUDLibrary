package com.example.sample.unit;

import com.example.sample.books.Books;
import com.example.sample.books.BooksRepository;
import com.example.sample.exceptions.NotFoundException;
import com.example.sample.users.Users;
import com.example.sample.users.UsersRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.AssertionErrors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application-unit.properties")
public class BooksControllerTest {
    @MockBean
    private BooksRepository booksRepo;
    @MockBean
    private UsersRepository usersRepo;
    @Autowired
    MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Books book = new Books("Abc", "Sienkiewicz", 321);

    @Test
    public void testUpdateBookTitleByAuthor() throws Exception {
        String patchBody = "[\n" +
                "    {\n" +
                "        \"op\":\"replace\",\n" +
                "        \"path\":\"/title\",\n" +
                "        \"value\":\"Krzyzacy\"\n" +
                "    }\n" +
                "]";
        when(booksRepo.findById(anyLong())).thenReturn(Optional.of(book));
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.patch("/api/books/1")
                .contentType("application/json-patch+json")
                .content(patchBody))
                .andExpect(status().isOk())
                .andReturn();
        Books patchedBook = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals("Krzyzacy", patchedBook.getTitle());
    }

    @Test
    public void testPostBook() throws Exception {
        when(booksRepo.save(book)).thenReturn(book);
        MvcResult result = mockMvc.perform(post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(book)))
                .andExpect(status().isCreated())
                .andReturn();
        Books postBook = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals(book, postBook);
    }

    @Test
    public void testGetBookById() throws Exception {
        when(booksRepo.findById(anyLong())).thenReturn(Optional.of(book));
        MvcResult result = mockMvc.perform(get("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Books foundBook = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals(book, foundBook);
    }

    @Test
    public void testGetBookByIdNotFound() throws Exception {
        book.setId(1L);
        given(booksRepo.findById(book.getId())).willThrow(new NotFoundException("Not found book with id "+ book.getId()));
        MvcResult result = mockMvc.perform(get("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Not found book with id "+ book.getId(), Objects.requireNonNull(result.getResolvedException()).getMessage());
    }

    @Test
    public void testReserveBook() throws Exception {
        when(booksRepo.findById(anyLong())).thenReturn(Optional.of(book));
        Users users = new Users("Pablo", "pablo@wp.pl", "123-123-1234", 34);
        users.setId(1L);
        when(usersRepo.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(users));
        MvcResult result = mockMvc.perform(put("/api/books/1/reserve/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Books reservedBook = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertNotNull(reservedBook.getUser_id());
    }

    @Test
    public void testCannotReserveBook() throws Exception {
        Users user = new Users("Pablo", "pablo@wp.pl", "123-123-1234", 34);
        user.setId(1L);
        book.setUser_id(user);
        given(booksRepo.findById(anyLong())).willReturn(Optional.of(book));
        MvcResult result = mockMvc.perform(put("/api/books/1/reserve/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().is(422))
                .andReturn();
        Books reservedBook = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertNotNull(reservedBook.getUser_id());
    }

    @Test
    public void testDeleteBookById() throws Exception {
        when(booksRepo.findById(anyLong())).thenReturn(Optional.of(book));
        mockMvc.perform(delete("/api/books/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}