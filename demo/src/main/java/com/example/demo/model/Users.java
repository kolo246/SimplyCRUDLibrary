package com.example.demo.model;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String username;

    public Users() {
        super();
    }

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
