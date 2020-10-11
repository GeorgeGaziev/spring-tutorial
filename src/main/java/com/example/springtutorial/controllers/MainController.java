package com.example.springtutorial.controllers;

import com.example.springtutorial.userClasses.Person;
import com.example.springtutorial.userClasses.PersonRepository;
import com.example.springtutorial.userClasses.Wish;
import com.example.springtutorial.userClasses.WishRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
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
    @Autowired
    private WishRepository wishRepository;

    @PostMapping("/person")
    public @ResponseBody
    long addPerson(@RequestBody Person person) {
        personRepository.save(person);
        return person.getId();
    }

    @GetMapping("/person/{id}")
    public @ResponseBody
    Person getPerson(@PathVariable long id) {
        return personRepository.findById(id);
    }

    @GetMapping(path = "/person")
    public @ResponseBody
    Iterable<Person> getAllUsers() {
        return personRepository.findAll();
    }

    @GetMapping(path = "/person/{id}/wishes")
    public @ResponseBody
    List<Wish> getUserWishes(@PathVariable long id) {
        Person person = personRepository.findById(id);
        return person.getWishList();
    }

    @PostMapping(path = "/person/{id}/wishes")
    public Wish addToWishlist(@PathVariable long id, @RequestBody String description) {
        Person person = personRepository.findById(id);

        List<Wish> wishList = person.getWishList();
        if (wishList == null) {
            wishList = new ArrayList<>();
        }
        Wish wish = new Wish(description);
        wishList.add(wish);
        person.setWishList(wishList);
        personRepository.save(person);

        return wishList.get(wishList.size()-1);
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
        } else throw new ResourceNotFoundException();

    }

    @DeleteMapping(path = "/person/{personId}/wishes/{wishId}")
    public void deleteWish(@PathVariable long personId, @PathVariable long wishId) {
        Person person = personRepository.findById(personId);
        List<Wish> wishList = person.getWishList();
        Optional<Wish> optWish = wishList.stream().filter(personWish -> personWish.getId() == wishId).findFirst();
        if (optWish.isPresent()) {
            Wish oldWish = optWish.get();
            wishList.remove(oldWish);
            person.setWishList(wishList);
            personRepository.save(person);
            wishRepository.deleteById(oldWish.getId());
        } else throw new ResourceNotFoundException();
    }

    @PostMapping(path = "/person/{id}/wishes/{wishId}/take")
    public void takeWish(@PathVariable long id, @PathVariable long wishId) {
        Person person = personRepository.findById(id);
        List<Wish> wishList = person.getWishList();
        Optional<Wish> optWish = wishList.stream().filter(personWish -> personWish.getId() == wishId).findFirst();
        if (optWish.isPresent()) {
            Wish oldWish = optWish.get();
            if (!oldWish.isTaken()){
                oldWish.setTaken(true);
            }
        } else throw new HttpServerErrorException(HttpStatus.NOT_FOUND);
    }

}