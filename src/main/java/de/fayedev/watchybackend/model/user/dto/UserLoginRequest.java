package de.fayedev.watchybackend.model.user.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class UserLoginRequest {

    @NotNull
    private String input;

    @NotNull
    private String password;
}
