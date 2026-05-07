package com.sip.book_shop.model;

import com.sip.book_shop.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "books")
public class Book implements BaseEntity {

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
