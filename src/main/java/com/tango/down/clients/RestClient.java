package com.tango.down.clients;

import com.tango.down.books.Books;
import com.tango.down.users.Users;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestClient {

    private RestTemplate restTemplate;

    public RestClient(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    //Users Rest Client
    public List<Users> getUsers(int pages, int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        ResponseEntity<List<Users>> responseEntity = restTemplate.exchange(
                "/api/users?pages=" + pages + "&size=" + size,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Users>>() {
                }
        );
        List<Users> list = responseEntity.getBody();
        list.forEach(System.out::println);
        return list;
    }

    public Users getUser(Long id) {
        return restTemplate.getForObject(
                "/api/users" + "/{id}", Users.class, id);
    }

    public ResponseEntity<Users> createUser(Users user) {
        return restTemplate.postForEntity("/api/users", user, Users.class);
    }

    //Unnsupported Media Type Error
    public ResponseEntity<Users> updateUser(Long id, String jsonPatch) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json-patch+json"));
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPatch, headers);
        return restTemplate.exchange(
                "/api/users/" + id,
                HttpMethod.PATCH,
                requestEntity,
                Users.class
        );
    }

    public void deleteUser(Long id) {
        restTemplate.delete("/api/users" + "/{id}", id);
    }

    //Books Rest Client
    public ResponseEntity<Books> createBook(Books book) {
        return restTemplate.postForEntity("/api/books", book, Books.class);
    }

    public Books fetchBookById(long id) {
        return restTemplate.getForObject("/api/books" + "/{id}", Books.class, id);
    }

    public List<Books> fetchAllBooks(int pages, int size, boolean borrow) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        ResponseEntity<List<Books>> responseEntity = restTemplate.exchange(
                "/api/books?pages=" + pages + "&size=" + size + "&borrow=" + borrow,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Books>>() {
                }
        );
        List<Books> listBooks = responseEntity.getBody();
        listBooks.forEach(System.out::println);
        return listBooks;
    }

    public ResponseEntity<Books> reserveBook(Long idBook, Long idUser) {
        Map<String, Long> variables = new HashMap<>();
        variables.put("id_books", idBook);
        variables.put("user_id", idUser);
        return restTemplate.exchange(
                "/api/books" + "/{id_books}/reserve/{user_id}",
                HttpMethod.PUT,
                null,
                Books.class,
                variables
        );
    }

    public ResponseEntity<Books> borrowBook(Long idBooks, Long idUser) {
        Map<String, Long> variables = new HashMap<>();
        variables.put("id_books", idBooks);
        variables.put("id_user", idUser);
        return restTemplate.exchange(
                "/api/books" + "/{id_books}/borrow/{id_user}",
                HttpMethod.PUT,
                null,
                Books.class,
                variables
        );
    }

    public ResponseEntity<Books> updateBookTitle(Long id, String jsonPatch) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json-patch+json");
        HttpEntity<String> requestEntity = new HttpEntity<>(jsonPatch,headers);
        return restTemplate.exchange(
                "/api/books/" + id,
                HttpMethod.PATCH,
                requestEntity,
                Books.class
        );
    }

    public void deleteBook(Long id) {
        restTemplate.delete("/api/books" + "/{id}", id);
    }
}
