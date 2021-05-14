package com.tango.down;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tango.down.books.Books;
import com.tango.down.books.BooksRepository;
import com.tango.down.clients.RestClient;
import com.tango.down.exceptions.NotFoundException;
import com.tango.down.users.Users;
import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureWebClient;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RunWith(SpringRunner.class)
@org.springframework.boot.test.autoconfigure.web.client.RestClientTest(RestClient.class)
public class RestClientTest {

    private static final String urlUsers = "/api/users";
    private static final String urlBooks = "/api/books";

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    private Users user = new Users("Pablo","pablo@wp.pl","123-123-4567",34);
    private Books book = new Books("Hobbit","Tolkien", 382);
    private String bodyUser = objectMapper.writeValueAsString(user);
    private String bodyBook = objectMapper.writeValueAsString(book);

    @Autowired
    private RestClient client;

    @Autowired
    private MockRestServiceServer serviceServer;

    public RestClientTest() throws JsonProcessingException {
    }

    @Test
    public void whenCallingCreateUser_thenGetResponseOK() throws JsonProcessingException {
        serviceServer.expect(requestTo(urlUsers)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bodyUser));
        ResponseEntity<Users> postUser = client.createUser(user);
        serviceServer.verify();
        assertThat(postUser.getStatusCodeValue()).isEqualTo(201);
        assertThat(postUser.getBody()).isEqualTo(user);
    }

    @Test
    public void whenCallingGetUser_thenGetResponseUser(){
        serviceServer.expect(requestTo(urlUsers + "/" + 1L))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bodyUser));
        Users responseUser = client.getUser(1L);
        serviceServer.verify();
        assertThat(responseUser).isNotNull();
    }

    @Test
    public void whenCallingGetUser_thenGetHttpClientErrorException(){
        serviceServer.expect(requestTo(urlUsers + "/" + anyLong())).andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertThrows(HttpClientErrorException.class, () -> client.getUser(anyLong()));
    }

    @Test
    public void whenCallingGetUsers_thenGetListOfUsers() throws JsonProcessingException {
        //input
        Integer pages = 0;
        Integer size = 1;
        List<Users> listUsers = List.of(user);
        String bodyUsers = objectMapper.writeValueAsString(listUsers);
        serviceServer.expect(requestTo(urlUsers + "?pages=" + pages + "&size=" + size))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bodyUsers));
        List<Users> listUser = client.getUsers(pages,size);
        serviceServer.verify();
        assertThat(listUser.size()).isEqualTo(size);
    }

    //UnsupportedMediaType Error !!
    @Test
    public void whenCallingUpdateUser_thenGetBackUpdateUser() throws JsonProcessingException {
        String jsonPatch = "[\n" +
                "    {\n" +
                "        \"op\":\"replace\",\n" +
                "        \"path\":\"/name\",\n" +
                "        \"value\":\"Pablo\"\n" +
                "    }\n" +
                "]";
        user.setName("Pablo");
        String updateBody = objectMapper.writeValueAsString(user);
        serviceServer.expect(requestTo(urlUsers + "/" + 1L))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.valueOf("application/json-patch+json"))
                                .body(updateBody));
        ResponseEntity<Users> updatedUser = client.updateUser(1L, jsonPatch);
        serviceServer.verify();
        assertThat(updatedUser.getBody().getName()).isEqualTo("Pablo");
    }

    @Test
    public void whenCallingCreateBook_thenGetOKAndBook(){
        serviceServer.expect(requestTo(urlBooks)).andExpect(method(HttpMethod.POST))
                .andRespond(withStatus(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bodyBook));
        ResponseEntity<Books> postedBook = client.createBook(book);
        serviceServer.verify();
        assertThat(postedBook.getStatusCodeValue()).isEqualTo(201);
        assertThat(postedBook.getBody()).isEqualTo(book);
    }

    @Test
    public void whenCallingFetchBookById_thenGetBackBook(){
        serviceServer.expect(requestTo(urlBooks + "/" + 1L)).andRespond(withSuccess(bodyBook, MediaType.APPLICATION_JSON));
        Books fetchBook = client.fetchBookById(1L);
        serviceServer.verify();
        assertThat(fetchBook).isNotNull();
    }

    @Test
    public void whenCallingFetchBookById_thenGetBackHttpClientErrorException(){
        serviceServer.expect(requestTo(urlBooks + "/" + anyLong())).andRespond(withStatus(HttpStatus.NOT_FOUND));
        assertThrows(HttpClientErrorException.class, () -> client.fetchBookById(anyLong()));
    }

    @Test
    public void whenCallingFetchAllBooks_thenGetListBooks() throws JsonProcessingException {
        //input
        Integer pages = 0;
        Integer size = 1;
        List<Books> listBooks = List.of(book);
        String bodyBooks = objectMapper.writeValueAsString(listBooks);
        boolean isBorrow = false;
        serviceServer.expect(requestTo(urlBooks + "?pages=" + pages + "&size=" + size + "&borrow=" + isBorrow))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(bodyBooks));
        List<Books> listUser = client.fetchAllBooks(pages,size,isBorrow);
        serviceServer.verify();
        assertThat(listUser.size()).isEqualTo(size);
    }

    @Test
    public void whenCallingReserveBook_thenGetOKAndBook() throws JsonProcessingException {
        Long idUser = 1L;
        Long idBooks = 1L;
        book.setUser_id(user);
        String reserveBook = objectMapper.writeValueAsString(book);
        serviceServer.expect(requestTo(urlBooks + "/" + idBooks + "/reserve/" + idUser))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(reserveBook));
        ResponseEntity<Books> responseBook = client.reserveBook(idBooks,idUser);
        serviceServer.verify();
        assertThat(responseBook.getBody().getUser_id()).isNotNull();
    }

    @Test
    public void whenCallingBorrowBooks_thenGetOKandBook() throws JsonProcessingException {
        Long idBook = 2L;
        Long idUser = 2L;
        book.setBorrow(true);
        String borrowBook = objectMapper.writeValueAsString(book);
        serviceServer.expect(requestTo(urlBooks + "/" + idBook + "/borrow/" + idUser))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(borrowBook));
        ResponseEntity<Books> responseBook = client.borrowBook(idBook,idUser);
        serviceServer.verify();
        assertThat(responseBook.getBody().isBorrow()).isTrue();
    }

    @Test
    public void whenCallingUpdateBook_thenGetOKAndBook() throws JsonProcessingException {
        String jsonPatch = "[\n" +
                "    {\n" +
                "        \"op\":\"replace\",\n" +
                "        \"path\":\"/title\",\n" +
                "        \"value\":\"Zemsta\"\n" +
                "    }\n" +
                "]";
        book.setTitle("Zemsta");
        String updateBody = objectMapper.writeValueAsString(book);
        serviceServer.expect(requestTo(urlBooks + "/" + 1L))
                .andExpect(method(HttpMethod.PATCH))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(updateBody));
        ResponseEntity<Books> updatedBook = client.updateBookTitle(1L, jsonPatch);
        serviceServer.verify();
        assertThat(book.getTitle()).isEqualTo(updatedBook.getBody().getTitle());
    }
}
