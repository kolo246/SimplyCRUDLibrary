package com.example.sample;

import com.example.sample.users.NotFoundException;
import com.example.sample.users.Users;
import com.example.sample.users.UsersRepository;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("classpath:application-unit.properties")
public class SampleTest {

    @MockBean
    private UsersRepository usersRepo;
    private final Users user = new Users("Wladek", "wladek@wp.pl", "123-456-7891", 24);

    @Test
    public void testSaveUser() {
        when(usersRepo.save(user)).thenReturn(user);
        Users saveUser = usersRepo.save(user);
        Assertions.assertEquals(saveUser.getName(), user.getName());
    }

    @Test
    public void testGetUseById() {
        Long id = 1L;
        Optional<Users> user = usersRepo.findByIdAndDeletedIsFalse(id);
        when(usersRepo.findByIdAndDeletedIsFalse(id)).thenReturn(user);
    }

    @Test
    public void testGetUserByIdNotFound() {
        Long id = 1L;
        given(usersRepo.findByIdAndDeletedIsFalse(id)).willThrow(new NotFoundException());
    }

    @Test
    public void testGetUserByName() {
        String name = "Pablo";
        Optional<Users> user = usersRepo.findByNameAndDeletedIsFalse(name);
        when(usersRepo.findByNameAndDeletedIsFalse(name)).thenReturn(user);
    }

    @Test
    public void testTestUserByNameNotFound() {
        String name = "Pablo";
        given(usersRepo.findByNameAndDeletedIsFalse(name)).willThrow(new NotFoundException());
    }
}