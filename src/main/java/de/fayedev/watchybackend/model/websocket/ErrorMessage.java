package de.fayedev.watchybackend.model.websocket;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ErrorMessage {

    private int status;

    private int code;

    private String message;
}
