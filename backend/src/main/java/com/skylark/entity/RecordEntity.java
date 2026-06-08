package com.skylark.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "record_entity")
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;
}
