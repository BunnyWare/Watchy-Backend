package de.fayedev.watchybackend.service;

import de.fayedev.watchybackend.model.settings.Settings;
import de.fayedev.watchybackend.repo.SettingsRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Optional;

@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;
    @Value("${tmdb.key}")
    private String tmdbKey;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    @PostConstruct
    public void init() {
        Optional<Settings> settings = settingsRepository.findById("default");

        if (settings.isEmpty()) {
            settingsRepository.save(new Settings("default", tmdbKey, true));
        }
    }

    public void setApiKey(String apiKey) {
        Settings settings = settingsRepository.getById("default");
        settings.setApiKey(apiKey);
        settingsRepository.save(settings);
    }

    public void setRegisterEnabled(boolean registerEnabled) {
        Settings settings = settingsRepository.getById("default");
        settings.setRegisterAllowed(registerEnabled);
        settingsRepository.save(settings);
    }

    public Settings getSettings() {
        return settingsRepository.getById("default");
    }
}
