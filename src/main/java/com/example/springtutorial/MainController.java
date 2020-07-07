package com.example.springtutorial;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class MainController {
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    private PersonRepository personRepository;

    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }

    @PostMapping("/person")
    public @ResponseBody
    Person addPerson(@RequestParam String firstName, @RequestParam String lastName) {
        Person person = new Person(firstName, lastName);
        personRepository.save(person);
        return person;
    }

    @GetMapping(path = "/person/all")
    public @ResponseBody
    Iterable<Person> getAllUsers() {
        return personRepository.findAll();
    }

    @GetMapping(path = "/person/{id}/wishes")
    public @ResponseBody
    Iterable<Wish> getUserWishes(@PathVariable long id) {
        Person person = personRepository.findById(id);
        return person.getWishList();

    }

    @PutMapping(path = "/person/{id}/wishes")
    public Wish addToWishlist(@PathVariable long id) {
        Person person = personRepository.findById(id);

        List<Wish> wishList = person.getWishList();
        if (wishList == null) {
            wishList = new ArrayList<>();
        }
        Wish wish = new Wish("test wish");
        wishList.add(wish);
        person.setWishList(wishList);
        personRepository.save(person);
        return wish;
    }

}