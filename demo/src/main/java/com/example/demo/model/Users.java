package com.example.demo.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;
    @Column(name = "user_name")
    private String username;
    public Users() {}
    public Users(String username) {
        this.username = username;
    }
    public UUID getId() {
        return id;
    }
    public void setId(UUID id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
