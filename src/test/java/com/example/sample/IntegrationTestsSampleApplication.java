package com.example.sample;

import com.example.sample.users.PagingRepository;
import com.example.sample.users.Users;
import com.example.sample.users.UsersController;
import com.example.sample.users.UsersRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.transaction.Transactional;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(initializers = IntegrationTestsSampleApplication.Initializer.class)
@AutoConfigureMockMvc
public class IntegrationTestsSampleApplication {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UsersRepository usersRepo;
    @Autowired
    private PagingRepository pagingRepo;
    @Autowired
    private final ObjectMapper objectMapper = new ObjectMapper();

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres")
            .withDatabaseName("postgres")
            .withUsername("user")
            .withPassword("user");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgres.getJdbcUrl(),
                    "spring.datasource.username=" + postgres.getUsername(),
                    "spring.datasource.password=" + postgres.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    public void testPagination() throws Exception {
        insertUsers();
        //test default values, size in both objects should be 5
        MvcResult result = mockMvc.perform(get("/api/users?pages&size"))
                .andExpect(status().isOk())
                .andReturn();
        List<Users> listUsers = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Users>>(){});
        Assertions.assertEquals(Integer.parseInt(UsersController.defaultSize),listUsers.size());
        //test random values for pages and size
        MvcResult randomResult = mockMvc.perform(get("/api/users").param("pages","0").param("size","3"))
                                    .andExpect(status().isOk())
                                    .andReturn();
        List<Users> randomList = objectMapper.readValue(randomResult.getResponse().getContentAsString(), new TypeReference<List<Users>>(){});
        Assertions.assertEquals(3,randomList.size());
        //test case if calling pagination multiple time, last call should return 0 element
        MvcResult mvcResult;
        mvcResult = mockMvc.perform(get("/api/users").param("pages","0").param("size","3"))
                .andExpect(status().isOk())
                .andReturn();
        listUsers = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Users>>(){});
        Assertions.assertEquals(3,listUsers.size());

        mvcResult = mockMvc.perform(get("/api/users").param("pages","1").param("size","3"))
                .andExpect(status().isOk())
                .andReturn();
        listUsers = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Users>>(){});
        Assertions.assertEquals(2,listUsers.size());

        mvcResult = mockMvc.perform(get("/api/users").param("pages","2").param("size","3"))
                .andExpect(status().isOk())
                .andReturn();
        listUsers = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<List<Users>>(){});
        Assertions.assertEquals(0,listUsers.size());
        //sprawdz czy zwraca alafabetycznie
    }

    @Test
    public void createUserInDB() throws Exception {
        Users user = new Users("Pablo", "pablo@wp.pl", "123-123-1234", 34);
        //create user in db and get result
        MvcResult result = mockMvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andReturn();
        //parsing json to obj
        Users responseUser = objectMapper.readValue(result.getResponse().getContentAsString(), Users.class);
        //checking values without ID because constructor of user not set id
        Assertions.assertEquals(responseUser.getName(), user.getName());
        Assertions.assertEquals(responseUser.getEmail(), user.getEmail());
        Assertions.assertEquals(responseUser.getPhoneNumber(), user.getPhoneNumber());
        Assertions.assertEquals(responseUser.getAge(), user.getAge());
        //test find user by id
        MvcResult getResult = mockMvc.perform(get("/api/users/{id}", responseUser.getId()))
                .andExpect(status().isOk())
                .andReturn();
        Users getUsers = objectMapper.readValue(getResult.getResponse().getContentAsString(), Users.class);
        //comparing objects, responseUser must be equal to getUsers
        Assertions.assertEquals(responseUser, getUsers);
        //test delete users by id, after that user is not found
        mockMvc.perform(delete("/api/users/{id}",responseUser.getId()))
                .andExpect(status().isOk());
        //user is not found
        mockMvc.perform(get("/api/users/{id}",responseUser.getId()))
                .andExpect(status().isNotFound());
    }

    private void insertUsers() {
        Users user = usersRepo.save(new Users("Pablo", "pablo@wp.pl", "123-123-1234", 34));
        usersRepo.save(new Users("Jasiek", "jasiek@wp.pl", "123-234-2345", 12));
        usersRepo.save(new Users("Wladek", "wladek@wp.pl", "123-345-3456", 45));
        usersRepo.save(new Users("Tadek", "tadek@wp.pl", "123-456-4567", 67));
        usersRepo.save(new Users("Andrzej", "andrzej@wp.pl", "123-156-5678", 19));
    }
}
