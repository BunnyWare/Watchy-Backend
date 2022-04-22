package de.fayedev.watchybackend.model.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class User {

    @Id
    private String uuid;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @JsonIgnore
    private String emailToken;

    private boolean emailConfirmed;

    @JsonIgnore
    @Column(nullable = false)
    private String password;

    @ElementCollection
    private List<Role> roles;

    @Transient
    private String token;

    private boolean showAdult;

    @Override
    public String toString() {
        return "User{" +
                "uuid='" + uuid + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return Objects.equals(uuid, user.uuid) && Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(uuid, username);
    }
}