package com.example.springtutorial;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */
@Entity
@Getter
@Setter
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String firstName;
    private String lastName;

    @ElementCollection
    @Cascade(org.hibernate.annotations.CascadeType.ALL)
    @OneToMany(targetEntity = Wish.class, fetch=FetchType.EAGER)
    private List<Wish> wishList = new ArrayList<>();

    public Person(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Person() {}

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id &&
                Objects.equals(firstName, person.firstName) &&
                Objects.equals(lastName, person.lastName) &&
                Objects.equals(wishList, person.wishList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, wishList);
    }
}