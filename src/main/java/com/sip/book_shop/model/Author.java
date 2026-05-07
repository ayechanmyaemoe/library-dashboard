package com.sip.book_shop.model;

import com.sip.book_shop.model.base.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "authors")
public class Author implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String name;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    public LocalDate birthDate;
}

