package com.example.springtutorial.userClasses;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

/**
 * @author g.gaziev (g.gaziev@itfbgroup.ru)
 */
@RepositoryRestResource(path = "person")
public interface PersonRepository extends CrudRepository<Person, Long> {

    List<Person> findByLastName(@Param("name") String name);

    Person findById(@Param("id") long id);

}