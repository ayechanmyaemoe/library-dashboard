package com.sip.book_shop.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "books",
        uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "published_year", "author_id", "category_id"})
})
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String title;
    public int publishedYear;

    @ManyToOne
    public Author author;

    @ManyToOne
    public Category category;
}
