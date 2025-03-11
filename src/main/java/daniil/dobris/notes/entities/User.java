package daniil.dobris.notes.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.List;


@Entity
@Table(name = "t_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 5, message = "Your username must be at least 5 characters")
    private String username;

    @Size(min = 5, message = "Your password must be at least 5 characters")
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "user")
    private List<Note> notes;

    @Transient
    private String passwordConfirm;

    public String getPassword() {
        return password;
    }
    public String getUsername() {
        return username;
    }
    public String getPasswordConfirm() {
        return passwordConfirm;
    }
    public List<Note> getNotes() {
        return notes;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public void setUsername(String username) { this.username = username; }
    public void setPasswordConfirm(String passwordConfirm) { this.passwordConfirm = passwordConfirm; }
    public void setNotes(List<Note> notes) { this.notes = notes; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
