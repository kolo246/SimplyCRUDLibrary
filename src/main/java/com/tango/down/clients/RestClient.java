package com.tango.down.clients;

import com.tango.down.books.Books;
import com.tango.down.users.Users;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RestClient {
    @Autowired
    private static RestTemplate restTemplate;
    private static final String urlUsers = "http://localhost:8080/api/users";
    private static final String urlBooks = "http://localhost:8080/api/books";

    public static void main(String[] args) {

    }

    //Users Rest Client
    private static List<Users> getUsers(int pages, int size) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(urlUsers)
                .queryParam("pages", pages)
                .queryParam("size", size);

        ResponseEntity<List<Users>> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Users>>() {
                }
        );
        List<Users> list = responseEntity.getBody();
        return list;
    }

    public static Users getUser(Long id) {
        return restTemplate.getForObject(
                urlUsers + "/{id}", Users.class, id);
    }

    public static ResponseEntity<Users> createUser(Users user) {
        return restTemplate.postForEntity(urlUsers, user, Users.class);
    }

    //Unnsupported Media Type Error
    public static Users updateUser(Long id, String jsonPatch) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.valueOf("application/json-patch+json"));
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

        return template.patchForObject(urlUsers + "/{id}", jsonPatch, Users.class, id);
    }

    public static void deleteUser(Long id) {
        restTemplate.delete(urlUsers + "/{id}", id);
    }

    //Books Rest Client
    public static ResponseEntity<Books> createBook(Books book) {
        return restTemplate.postForEntity(urlBooks, book, Books.class);
    }

    public static Books fetchBookById(long id) {
        return restTemplate.getForObject(urlBooks + "/{id}", Books.class, id);
    }

    public static List<Books> fetchAllBooks(int pages, int size, boolean borrow) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(urlBooks)
                .queryParam("pages", pages)
                .queryParam("size", size)
                .queryParam("borrow", borrow);
        ResponseEntity<List<Books>> responseEntity = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Books>>() {
                }
        );
        List<Books> listBooks = responseEntity.getBody();
        return listBooks;
    }

    public static ResponseEntity<Books> reserveBook(Long idBook, Long idUser) {
        Map<String, Long> variables = new HashMap<>();
        variables.put("id_books", idBook);
        variables.put("user_id", idUser);
        return restTemplate.exchange(
                urlBooks + "/{id_books}/reserve/{user_id}",
                HttpMethod.PUT,
                null,
                Books.class,
                variables
        );
    }

    public static ResponseEntity<Books> borrowBook(Long idBooks, Long idUser) {
        Map<String, Long> variables = new HashMap<>();
        variables.put("id_books", idBooks);
        variables.put("id_user", idUser);
        return restTemplate.exchange(
                urlBooks + "/{id_books}/borrow/{id_user}",
                HttpMethod.PUT,
                null,
                Books.class,
                variables
        );
    }

    public static Books updateBookTitle(Long id, String jsonPatch) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, "application/json-patch+json");
        ClientHttpRequestFactory requestFactory;
        RestTemplate template = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
        return template.patchForObject(
                urlBooks + "/{id}", jsonPatch, Books.class, id
        );
    }

    public static void deleteBook(Long id) {
        restTemplate.delete(urlBooks + "/{id}", id);
    }
}
