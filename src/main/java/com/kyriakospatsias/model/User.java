package com.kyriakospatsias.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", schema = "memorandum")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String congitoId;
    private String username;
    private String firstName;
    private String lastName;    
    private String timezone;
    private String status;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;

    
}
