package de.fayedev.watchybackend.model.user.dto;

import de.fayedev.watchybackend.model.user.Role;
import de.fayedev.watchybackend.model.user.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class UserResponse {

    private String uuid;

    private String username;

    private String email;

    private boolean emailConfirmed;

    private List<Role> roles;

    private String token;


    public UserResponse(User user) {
        this.uuid = user.getUuid();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.emailConfirmed = user.isEmailConfirmed();
        this.roles = user.getRoles();
        this.token = user.getToken();
    }
}
