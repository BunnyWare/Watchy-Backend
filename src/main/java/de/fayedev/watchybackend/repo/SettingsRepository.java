package de.fayedev.watchybackend.repo;

import de.fayedev.watchybackend.model.settings.Settings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<Settings, String> {
}
