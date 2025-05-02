package com.uttkarsh.InstaStudio.entities;

import com.uttkarsh.InstaStudio.entities.enums.UserType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "App_User")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firebaseId;

    private String name;

    private String email;

    private String phoneNo;

    @Enumerated(EnumType.STRING)
    private UserType userType;

}
