package de.fayedev.watchybackend.model.settings.dto;

import lombok.Data;

@Data
public class UserRegistrationEnabledUpdateRequest {

    private boolean registrationEnabled;
}
