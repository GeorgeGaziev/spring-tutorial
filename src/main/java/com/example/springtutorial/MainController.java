package com.example.springtutorial;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
public class MainController {

    @Autowired
    private PersonRepository personRepository;

    @PostMapping("/person")
    public @ResponseBody
    long addPerson(@RequestBody Person person) {
        personRepository.save(person);
        return person.getId();
    }

    @GetMapping("/person/{io}")
    public @ResponseBody
    Person getPerson(@PathVariable long id) {
        return personRepository.findById(id);
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

    @PostMapping(path = "/person/{id}/wishes")
    public Wish addToWishlist(@PathVariable long id, @RequestBody Wish wish) {
        Person person = personRepository.findById(id);

        List<Wish> wishList = person.getWishList();
        if (wishList == null) {
            wishList = new ArrayList<>();
        }
        wishList.add(wish);
        person.setWishList(wishList);
        personRepository.save(person);
        return wish;
    }

    @PostMapping(path = "/person/{id}/wishes/{wishId}")
    public Wish editWish(@PathVariable long id, @RequestBody Wish newWish, @PathVariable long wishId) {
        Person person = personRepository.findById(id);

        List<Wish> wishList = person.getWishList();
        Optional<Wish> optWish = wishList.stream().filter(personWish -> personWish.getId() == wishId).findFirst();
        if (optWish.isPresent()) {
            Wish oldWish = optWish.get();
            oldWish.setDescription(newWish.getDescription());
            oldWish.setTaken(newWish.isTaken());
            return oldWish;
        } else throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
    }

}