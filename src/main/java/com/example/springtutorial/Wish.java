package com.example.springtutorial;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */
@Entity
@Getter
@Setter
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String description;
    private boolean taken;

    public Wish(String description) {
        this.description = description;
        this.taken = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Wish wish = (Wish) o;
        return id == wish.id &&
                taken == wish.taken &&
                Objects.equals(description, wish.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, description, taken);
    }

    public Wish(){}
}
