package com.paloit.training.sp01.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "user_account")
public class User {
    @Id
    @JsonIgnore
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator"
    )
    private UUID id;

    @Column(unique = true)
    private String email;

    @JsonIgnore
    private String password;
    private String firstName;
    private String lastName;

    @JsonIgnoreProperties("user")
    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;

}
