package ru.drsdgdbye.basic_authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.drsdgdbye.basic_authentication.domain.User;
import ru.drsdgdbye.basic_authentication.repository.UserRepository;
import ru.drsdgdbye.basic_authentication.service.dto.SuccessDto;
import ru.drsdgdbye.basic_authentication.service.dto.UserDto;

import javax.transaction.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = BasicAuthenticationApplication.class)
class BasicAuthenticationApplicationTests {

    private static final String DEFAULT_LOGIN = "johndoe";

    private static final String DEFAULT_NAME = "john doe";
    private static final String UPDATED_NAME = "john smith";

    private static final String DEFAULT_PASSWORD = "Passjohnd0e";
    private static final String UPDATED_PASSWORD = "Passj0hnsmith";
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc restUserMockMvc;
    private User user;

    public static User createEntity() {
        User user = new User();
        user.setLogin(DEFAULT_LOGIN);
        user.setName(DEFAULT_NAME);
        user.setPassword(DEFAULT_PASSWORD);
        user.setRoles(new HashSet<>());
        return user;
    }

    @BeforeEach
    public void initTest() {
        user = createEntity();
    }

    private void assertPersistedUsers(Consumer<List<User>> userAssertion) {
        userAssertion.accept(userRepository.findAll());
    }

    @Test
    @Transactional
    void testAddEndpointUser() throws Exception {
        int databaseSizeBeforeCreate = userRepository.findAll().size();

        UserDto user = new UserDto();
        user.setLogin(DEFAULT_LOGIN);
        user.setName(DEFAULT_NAME);
        user.setPassword(DEFAULT_PASSWORD);
        user.setRoles(new HashSet<>());
        restUserMockMvc.perform(
                post("/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isCreated())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessDto())));

        assertPersistedUsers(users -> {
            assertThat(users).hasSize(databaseSizeBeforeCreate + 1);
            User testUser = users.get(users.size() - 1);
            assertThat(testUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
            assertThat(testUser.getName()).isEqualTo(DEFAULT_NAME);
            assertThat(testUser.getPassword()).isEqualTo(DEFAULT_PASSWORD);
            assertThat(testUser.getRoles()).isEqualTo(new HashSet<>());
        });
    }

    @Test
    @Transactional
    void testEditEndpointUser() throws Exception {
        userRepository.saveAndFlush(user);
        User updatedUser = userRepository.findById(user.getId()).get();

        UserDto userDto = new UserDto();
        userDto.setId(updatedUser.getId());
        userDto.setLogin(DEFAULT_LOGIN);
        userDto.setName(UPDATED_NAME);
        userDto.setPassword(UPDATED_PASSWORD);
        userDto.setRoles(new HashSet<>());
        restUserMockMvc.perform(
                put("/edit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(new SuccessDto())));

        assertPersistedUsers(users -> {
            User testUser = users.get(users.size() - 1);
            assertThat(testUser.getLogin()).isEqualTo(DEFAULT_LOGIN);
            assertThat(testUser.getName()).isEqualTo(UPDATED_NAME);
            assertThat(testUser.getPassword()).isEqualTo(UPDATED_PASSWORD);
            assertThat(testUser.getRoles()).isEqualTo(new HashSet<>());
        });
    }

    @Test
    @Transactional
    void testDeleteEndpointUser() throws Exception {
        userRepository.saveAndFlush(user);
        int databaseSizeBeforeDelete = userRepository.findAll().size();

        restUserMockMvc.perform(delete("/delete/{id}", user.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        assertPersistedUsers(users -> assertThat(users).hasSize(databaseSizeBeforeDelete - 1));
    }

}
