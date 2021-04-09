package com.example.sample;

import com.example.sample.users.PagingRepository;
import com.example.sample.users.Users;
import com.example.sample.users.UsersRepository;
import org.junit.ClassRule;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

import javax.transaction.Transactional;
import java.util.List;

@SpringBootTest
@ContextConfiguration(initializers = IntegrationTestsSampleApplication.Initializer.class)
public class IntegrationTestsSampleApplication {

    @MockBean
    private UsersRepository usersRepo;
    @MockBean
    private PagingRepository pagingRepo;

    @ClassRule
    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres")
            .withDatabaseName("postgres")
            .withUsername("user")
            .withPassword("user");

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext>{
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url="+postgres.getJdbcUrl(),
                "spring.datasource.username="+postgres.getUsername(),
                "spring.datasource.password="+postgres.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    @Transactional
    public void givenUsersInDB(){
        insertUsers();
        PageRequest page = PageRequest.of(0, 5);
        List<Users> usersList = pagingRepo.findAll(page);


    }

    private void insertUsers(){
        usersRepo.save(new Users("Pablo","pablo@wp.pl","123-123-1234",34));
        usersRepo.save(new Users("Jasiek","jasiek@wp.pl","123-234-2345",12));
        usersRepo.save(new Users("Wladek","wladek@wp.pl","123-345-3456",45));
        usersRepo.save(new Users("Tadek","tadek@wp.pl","123-456-4567",67));
        usersRepo.save(new Users("Andrzej","andrzej@wp.pl","123-156-5678",19));
    }
}
