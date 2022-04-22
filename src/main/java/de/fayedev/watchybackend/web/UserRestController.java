package de.fayedev.watchybackend.web;

import de.fayedev.watchybackend.exception.ApplicationException;
import de.fayedev.watchybackend.model.user.dto.UserLoginRequest;
import de.fayedev.watchybackend.model.user.dto.UserMailConfirmRequest;
import de.fayedev.watchybackend.model.user.dto.UserRegisterRequest;
import de.fayedev.watchybackend.model.user.dto.UserResponse;
import de.fayedev.watchybackend.service.UserService;
import de.fayedev.watchybackend.utils.SecurityAccessor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API")
public class UserRestController {

    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    @Operation(summary = "Get user.")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))})
    public UserResponse getUser() throws ApplicationException {
        return userService.getUserResponseByUserName(SecurityAccessor.getAuthenticatedUserName());
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user.")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))})
    public UserResponse register(@RequestBody @Valid UserRegisterRequest body) throws ApplicationException {
        return userService.register(body);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user.")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))})
    public UserResponse login(@RequestBody @Valid UserLoginRequest body) throws ApplicationException {
        return userService.login(body);
    }

    @GetMapping("/confirm")
    @Operation(summary = "Confirm user email.")
    public void confirm(@RequestParam("emailToken") String emailToken) throws ApplicationException {
        userService.confirm(emailToken);
    }

    @PostMapping("/resendConfirm")
    @Operation(summary = "Resend user confirm email.")
    public void sendConfirm(@RequestBody @Valid UserMailConfirmRequest body) throws ApplicationException {
        userService.sendConfirm(body);
    }
}
