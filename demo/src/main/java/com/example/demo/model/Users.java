package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "person")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_name")
    private String username;
    public Users() {
        super();
    }
    public Users(String username) {
        this.username = username;
    }
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
}
