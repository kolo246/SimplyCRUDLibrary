package com.example.sample.books;

import com.example.sample.users.Users;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@Entity
@Table(name = "books")
public class Books {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @NotNull
    @Column(name = "title")
    private String title;
    @NotNull
    @Column(name = "author")
    private String author;
    @NotNull
    @Column(name = "pages")
    private Integer pages;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user_id;

    public Books(String title, String author, Integer pages) {
        this.title = title;
        this.author = author;
        this.pages = pages;
    }
}