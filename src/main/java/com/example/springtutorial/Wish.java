package com.example.springtutorial;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */
@Entity
@Getter
@Setter
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String description;
    private boolean taken;

    public Wish(String description) {
        this.description = description;
        this.taken = false;
    }

    public Wish(){}
}
