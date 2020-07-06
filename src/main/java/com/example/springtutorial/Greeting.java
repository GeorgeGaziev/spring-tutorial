package com.example.springtutorial;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */
@Entity
public class Greeting {

    @Id
    private final long id;
    private final String content;

    public Greeting(long id, String content) {
        this.id = id;
        this.content = content;
    }

    public long getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}