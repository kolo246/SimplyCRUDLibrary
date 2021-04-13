package com.example.sample;

import com.example.sample.users.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SampleApplicationTests {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UsersRepository usersRepo;
    @MockBean
    private PagingRepository paginRepo;
    private final Users user = new Users("Wladek","wladek@wp.pl","123-456-7891",24);

    @Test
    public void testGetUsersWithPagination() throws Exception {
        Long id = 1L;
        PageRequest page = PageRequest.of(0,5);
        List<Users> list = paginRepo.findAll(page);
        when(paginRepo.findAll(page)).thenReturn(list);
        mockMvc.perform(get("/api/users?pages=0&size=10")).andExpect(status().isOk());
    }

    @Test
    public void testGetUseById() throws Exception {
        Long id = 1L;
        Optional<Users> user  = usersRepo.findByIdAndDeletedIsFalse(id);
        when(usersRepo.findByIdAndDeletedIsFalse(id)).thenReturn(user);
    }

    @Test
    public void testGetUserByIdNotFound(){
        Long id = 1L;
        given(usersRepo.findByIdAndDeletedIsFalse(id)).willThrow(new NotFoundException());
    }

    @Test
    public void testGetUserByName(){
        String name = "Pablo";
        Optional<Users> user = usersRepo.findByNameAndDeletedIsFalse(name);
        when(usersRepo.findByNameAndDeletedIsFalse(name)).thenReturn(user);
    }

    @Test
    public void testTestUserByNameNotFound(){
        String name = "Pablo";
        given(usersRepo.findByNameAndDeletedIsFalse(name)).willThrow(new NotFoundException());
    }

    @Test
    public void testPostUser() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        when(usersRepo.save(user)).thenReturn(user);
        mockMvc.perform(post("/api/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }
}