package ru.drsdgdbye.basic_authentication.service;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ru.drsdgdbye.basic_authentication.domain.User;
import ru.drsdgdbye.basic_authentication.repository.RoleRepository;
import ru.drsdgdbye.basic_authentication.repository.UserRepository;
import ru.drsdgdbye.basic_authentication.security.exceptions.UserNotFoundException;
import ru.drsdgdbye.basic_authentication.service.dto.UserDto;
import ru.drsdgdbye.basic_authentication.service.dto.UserWithoutRolesDto;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service class for managing users.
 */
@Log4j2
@Service
@Transactional
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserService(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Create new user.
     *
     * @param userDto new user.
     */
    public void createUser(UserDto userDto) {
        log.debug("create user from userDto: {}", userDto);

        User newUser = new User();
        newUser.setLogin(userDto.getLogin().toLowerCase().strip());
        newUser.setName(userDto.getName());
        newUser.setPassword(userDto.getPassword());
        userDto.getRoles().stream()
                .map(roleRepository::getOne)
                .forEach(newUser::addRole);

        userRepository.save(newUser);
    }

    /**
     * Update all information for a specific user.
     *
     * @param userDto user to update.
     */
    public void updateUser(UserDto userDto) {
        log.debug("update user from userDto: {}", userDto);

        User updateUser = userRepository.findById(userDto.getId()).orElseThrow(UserNotFoundException::new);
        updateUser.setName(userDto.getName());
        updateUser.setPassword(userDto.getPassword());

        updateUser.setRoles(userDto.getRoles().stream()
                .map(roleRepository::getOne)
                .collect(Collectors.toSet()));

        userRepository.save(updateUser);
    }

    /**
     * Get a list of all the users.
     *
     * @return a list of all the users.
     */
    public List<UserWithoutRolesDto> getUsersList() {
        log.debug("get list of users");

        return userRepository.findAll().stream()
                .map(UserWithoutRolesDto::new)
                .collect(Collectors.toList());
    }

    /**
     * Get a specific user by id.
     *
     * @return the user.
     */
    public UserDto getUser(Long id) {
        log.debug("get one user by id: {}", id);

        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return new UserDto(user);
    }

    public void deleteUser(Long id) {
        log.debug("delete user by id: {}", id);

        User deleteUser = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.delete(deleteUser);
    }

    public boolean isUserExists(UserDto userDto) {
        log.debug("check if user exists by userDto: {}", userDto);

        String login = userDto.getLogin().toLowerCase().strip();
        User currentUser = userRepository.findOneByLogin(login).orElse(null);
        return !Objects.isNull(currentUser);
    }
}
