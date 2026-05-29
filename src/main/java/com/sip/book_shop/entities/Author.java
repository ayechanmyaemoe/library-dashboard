package com.sip.book_shop.entities;

import com.sip.book_shop.constant.Constant;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@Table(name = "authors")
public class Author {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    public String name;

    @DateTimeFormat(pattern = Constant.DB_DATE_FORMAT)
    public LocalDate birthDate;
}

