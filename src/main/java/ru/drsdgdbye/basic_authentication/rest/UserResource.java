package ru.drsdgdbye.basic_authentication.rest;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.drsdgdbye.basic_authentication.security.exceptions.UserAlreadyExistsException;
import ru.drsdgdbye.basic_authentication.security.exceptions.UserNotFoundException;
import ru.drsdgdbye.basic_authentication.service.UserService;
import ru.drsdgdbye.basic_authentication.service.dto.SuccessDto;
import ru.drsdgdbye.basic_authentication.service.dto.UserDto;
import ru.drsdgdbye.basic_authentication.service.dto.UserWithoutRolesDto;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Objects;

/**
 * REST controller for managing users.
 */
@Log4j2
@RestController
public class UserResource {

    private final UserService userService;

    public UserResource(UserService userService) {
        this.userService = userService;
    }

    /**
     * {@code POST  /add}  : Creates a new user.
     * <p>
     * Creates a new user if the login is not already used and id is null.
     *
     * @param userDto the user to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body SuccessDto,
     * or with status {@code 400 (Bad Request)} if the login is already in use or have validate exceptions.
     * @throws UserAlreadyExistsException {@code 409 (Conflict)} if the login is already in use.
     */
    @PostMapping("/add")
    public ResponseEntity<SuccessDto> addUser(@Valid @RequestBody UserDto userDto) {
        log.debug("rest request to add user: {}", userDto);

        if (userService.isUserExists(userDto) || !Objects.isNull(userDto.getId())) {
            throw new UserAlreadyExistsException();
        }
        userService.createUser(userDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SuccessDto());
    }

    /**
     * {@code PUT /edit} : Updates an existing User.
     *
     * @param userDto the user to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with SuccessDto body.
     * @throws UserNotFoundException {@code 404 (Not Found)} if the User not found.
     */
    @PutMapping("/edit")
    public ResponseEntity<SuccessDto> editUser(@Valid @RequestBody UserDto userDto) {
        log.debug("rest request to edit user: {}", userDto);

        if (!userService.isUserExists(userDto) || Objects.isNull(userDto.getId())) {
            throw new UserNotFoundException();
        }
        userService.updateUser(userDto);
        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SuccessDto());
    }

    /**
     * {@code GET /list} : get all users.
     *
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body all users.
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserWithoutRolesDto>> getUsers() {
        log.debug("rest request to get list of users without roles");

        return ResponseEntity.ok(userService.getUsersList());
    }

    /**
     * {@code GET /get/:id} : get the user by id.
     *
     * @param id the id of the user to find.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the user, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/get/{id}")
    public ResponseEntity<UserDto> getUser(@NotEmpty @Positive @PathVariable Long id) {
        log.debug("rest request to get user with roles by id: {}", id);

        return ResponseEntity.ok(userService.getUser(id));
    }

    /**
     * {@code DELETE /delete/:id} : delete the User.
     *
     * @param id the id of the user to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteUser(@NotEmpty @Positive @PathVariable Long id) {
        log.debug("rest request to delete user by id: {}", id);

        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
