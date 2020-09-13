package ru.drsdgdbye.basic_authentication.service.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.drsdgdbye.basic_authentication.domain.Role;
import ru.drsdgdbye.basic_authentication.domain.User;
import ru.drsdgdbye.basic_authentication.utils.Constants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor //empty constructor for jackson
public class UserDto {

    private Long id;

    @Size(max = 32)
    @NotBlank(message = "login must not be null or empty")
    private String login;

    /**
     * this weird construction with @JsonIgnore to set password in entity without reading from
     **/
    @Size(max = 64)
    @Setter(onMethod_ = {@JsonIgnore})
    @Getter(onMethod_ = {@JsonProperty})
    @NotBlank(message = "password must not be null or empty")
    @Pattern(regexp = Constants.PASSWORD_REGEX, message = "password must have >0 alphabetic in upper case and number. size must be >=4")
    private String password;

    @Size(max = 32)
    @NotBlank(message = "name must not be null or empty")
    private String name;

    @NotNull(message = "roles must not be null")
    private Set<Long> roles = new HashSet<>();

    public UserDto(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.password = user.getPassword();
        this.name = user.getName();
        this.roles.addAll(user.getRoles().stream()
                .map(Role::getId)
                .collect(Collectors.toSet()));
    }
}
