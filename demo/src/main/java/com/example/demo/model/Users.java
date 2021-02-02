package com.example.demo.model;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "user_name")
    private String userName;
    public Users() {}
    public Users(String username) {
        this.userName = username;
    }
    public long getId() {
        return id;
    }
    public String getUserName() {
        return userName;
    }
}
