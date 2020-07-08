package com.example.springtutorial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */
@Entity
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String firstName;
    private String lastName;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OneToMany(targetEntity = Wish.class)
    private List<Wish> wishList = new ArrayList<>();

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person() {}
}