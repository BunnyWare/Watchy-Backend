package de.fayedev.watchybackend.model.settings;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Settings {

    @Id
    private String id;

    private String apiKey;

    private boolean registerAllowed;
}
