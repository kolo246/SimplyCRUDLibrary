package com.tango.down.unit;

import com.tango.down.exceptions.NotFoundException;
import com.tango.down.users.Users;
import com.tango.down.users.UsersRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Objects;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
@TestPropertySource("classpath:application-unit.properties")
public class UsersControllerTest {

    @MockBean
    private UsersRepository usersRepo;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Users user = new Users("Vladek", "wladek@wp.pl", "123-456-7891", 24);

    @Test
    public void testPostUser() throws Exception {
        when(usersRepo.save(user)).thenReturn(user);
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andReturn();
        Users postedUser = objectMapper.readValue(result
                .getResponse()
                .getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals(user.getName(), postedUser.getName());
        Assertions.assertEquals(user.getEmail(), postedUser.getEmail());
        Assertions.assertEquals(user.getPhoneNumber(), postedUser.getPhoneNumber());
        Assertions.assertEquals(user.getAge(), postedUser.getAge());
    }

    @Test
    public void testGetUserById() throws Exception {
        when(usersRepo.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(user));
        MvcResult result = mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
        Users findUser = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals(user.getName(), findUser.getName());
        Assertions.assertEquals(user.getEmail(), findUser.getEmail());
        Assertions.assertEquals(user.getPhoneNumber(), findUser.getPhoneNumber());
        Assertions.assertEquals(user.getAge(), findUser.getAge());
    }

    @Test
    public void testGetUserByIdNotFound() throws Exception {
        user.setId(1L);
        given(usersRepo.findById(user.getId())).willThrow(new NotFoundException("Not found user with id " + user.getId()));
        MvcResult result = mockMvc.perform(get("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        Assertions.assertEquals("Not found user with id " + user.getId(), Objects.requireNonNull(result.getResolvedException()).getMessage());
    }

    @Test
    public void testPatchUsers() throws Exception {
        String patchBody = "[\n" +
                "    {\n" +
                "        \"op\":\"replace\",\n" +
                "        \"path\":\"/name\",\n" +
                "        \"value\":\"Pablo\"\n" +
                "    }\n" +
                "]";
        when(usersRepo.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(user));
        MvcResult result = mockMvc.perform(patch("/api/users/1")
                .contentType("application/json-patch+json")
                .content(patchBody))
                .andExpect(status().isOk())
                .andReturn();
        Users patchedUser = objectMapper.readValue(result.getResponse()
                .getContentAsString(), new TypeReference<>() {
        });
        Assertions.assertEquals("Pablo", patchedUser.getName());
    }

    @Test
    public void testDeleteUser() throws Exception {
        when(usersRepo.findByIdAndDeletedIsFalse(anyLong())).thenReturn(Optional.of(user));
        mockMvc.perform(delete("/api/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertTrue(user.isDeleted());
    }
}