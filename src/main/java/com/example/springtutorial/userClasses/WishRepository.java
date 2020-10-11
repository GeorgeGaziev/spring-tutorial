package com.example.springtutorial.userClasses;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(path = "wish")
public interface WishRepository extends CrudRepository<Wish, Long> {
}
