package com.example.springtutorial;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
//@DataJpaTest
class SpringTutorialApplicationTests {

    Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MainController mainController;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Add new person")
    void addPerson() throws Exception {
        Person testPerson = new Person("Beep", "Boop" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        String json = gson.toJson(testPerson);

        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Get person detail info")
    void getPersonInfo() throws Exception {
        Person testPerson = new Person("Beep" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "Boop" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        long personId = mainController.addPerson(testPerson);

        MvcResult result = this.mockMvc.perform(get("/person/" + personId))
                .andDo(print())
                .andExpect(status().isOk()).andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Person person = objectMapper.readValue(contentAsString, Person.class);

        Assertions.assertEquals(person, testPerson);
    }


    @Test
    @DisplayName("Get list persons")
    void getAllPersons() throws Exception {

        Person testPerson = new Person("Beep" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "Boop" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        String json = gson.toJson(testPerson);

        this.mockMvc.perform(post("/person")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/person/all"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", isA(ArrayList.class)))
                .andExpect(jsonPath("$.*", hasSize(greaterThan(0))))
                .andExpect(jsonPath("$[-1].firstName", equalTo(testPerson.getFirstName())))
                .andExpect(jsonPath("$[-1].lastName", equalTo(testPerson.getLastName())));
    }

    @Test
    @DisplayName("Add wish to a person")
    void addPersonWish() throws Exception {
        Person testPerson = new Person("Beep" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "Boop" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));

        long personId = mainController.addPerson(testPerson);
        Wish wish = new Wish("test_wish");

        String json = gson.toJson(wish);

        MvcResult result = this.mockMvc.perform(post("/person/" + personId + "/wishes")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Wish createdWish = objectMapper.readValue(contentAsString, Wish.class);

        Assertions.assertEquals(wish, createdWish);
    }

    @Test
    @DisplayName("Edit person's wish")
    void editPersonWish() throws Exception {
        Person testPerson = new Person("Beep" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                "Boop" + LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
        ArrayList<Wish> listOfWishes = new ArrayList<Wish>() {
            {
                new Wish("test_wish_1");
                new Wish("test_wish_2");
                new Wish("test_wish_3");
            }
        };

        testPerson.setWishList(listOfWishes);
        long personId = mainController.addPerson(testPerson);
        Person savedPerson = mainController.getPerson(personId);
        List<Wish> wishListToUpdate = savedPerson.getWishList();
        //todo empty wishlist
        Wish wishToUpdate = wishListToUpdate.get(0);
        wishToUpdate.setDescription("updated_description");

        String json = gson.toJson(wishToUpdate);


        MvcResult result = this.mockMvc.perform(post("/person/" + personId + "/wishes")
                .contentType(MediaType.APPLICATION_JSON).content(json))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        String contentAsString = result.getResponse().getContentAsString();
        Wish updatedWish = objectMapper.readValue(contentAsString, Wish.class);

        Assertions.assertEquals(updatedWish, wishToUpdate);
    }

}
