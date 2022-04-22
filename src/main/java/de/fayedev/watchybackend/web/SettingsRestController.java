package de.fayedev.watchybackend.web;

import de.fayedev.watchybackend.exception.ApplicationException;
import de.fayedev.watchybackend.model.settings.Settings;
import de.fayedev.watchybackend.model.settings.dto.DbKeyUpdateRequest;
import de.fayedev.watchybackend.model.settings.dto.UserRegistrationEnabledUpdateRequest;
import de.fayedev.watchybackend.service.SettingsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/settings")
@Tag(name = "Settings API")
public class SettingsRestController {

    private final SettingsService settingsService;

    public SettingsRestController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @PostMapping("/dbkey")
    @Operation(summary = "Set a new api key.")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void setKey(@RequestBody @Valid DbKeyUpdateRequest body) throws ApplicationException {
        settingsService.setApiKey(body.getDbKey());
    }

    @PostMapping("/registration")
    @Operation(summary = "Enable or disable registration of new users.")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public void setRegisterEnabled(@RequestBody @Valid UserRegistrationEnabledUpdateRequest body) throws ApplicationException {
        settingsService.setRegisterEnabled(body.isRegistrationEnabled());
    }

    @GetMapping
    @Operation(summary = "Returns current settings")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @ApiResponse(responseCode = "200", content = {@Content(mediaType = "application/json", schema = @Schema(implementation = Settings.class))})
    public Settings getSettings() throws ApplicationException {
        return settingsService.getSettings();
    }
}
