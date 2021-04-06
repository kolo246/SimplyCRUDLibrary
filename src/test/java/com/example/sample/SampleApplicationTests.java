package com.example.sample;

import com.example.sample.users.NotFoundException;
import com.example.sample.users.Users;
import com.example.sample.users.UsersController;
import com.example.sample.users.UsersRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SampleApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UsersController usersController;
    @MockBean
    private UsersRepository usersRepo;
    private final Users user = new Users("Wladek","wladek@wp.pl","123-456-7891",24);
    @Test
    public void testGetAllUsers() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetUserById() throws Exception {
        mockMvc.perform(get("/api/users/id/1"))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetUserByName() throws Exception {
        mockMvc.perform(get("/api/users/name/Dru"))
                .andExpect(status().isOk());
    }
    @Test
    public void testNotFoundUserById() throws Exception {
        when(usersController.getUserById(0L)).thenReturn(null);
        mockMvc.perform(get("/api/users/id/0"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testNotFoundUserByName() throws Exception {
        given(usersController.getUserByName("Dro")).willReturn(null);
        mockMvc.perform(get("/api/users/name/Dro"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testPostUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        given(usersController.postUser(user)).willReturn(user);
        mockMvc.perform(post("/api/users")
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .contentType(objectMapper.writeValueAsString(
                        new Users("Jozek","jozek@wp.pl","123-123-1234",35))))
                .andExpect(status().isOk());
    }
    @Test
    public void testGetFiveUsers() throws Exception {
        List<Users> usersList = usersController.getFiveUsers();
        given(usersController.getFiveUsers()).willReturn(usersList);
        mockMvc.perform(get("/api/users/page"))
                .andExpect(status().isOk());
    }
    @Test
    public void testDeleteUserById() throws Exception {
        mockMvc.perform(delete("api/users/1"))
                .andExpect(status().isOk());
    }
}