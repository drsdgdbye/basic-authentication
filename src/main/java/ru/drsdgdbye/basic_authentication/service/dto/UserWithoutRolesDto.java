package ru.drsdgdbye.basic_authentication.service.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.drsdgdbye.basic_authentication.domain.User;

@Getter
@NoArgsConstructor
public class UserWithoutRolesDto {
    private Long id;
    private String login;
    private String name;

    public UserWithoutRolesDto(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.name = user.getName();
    }
}
